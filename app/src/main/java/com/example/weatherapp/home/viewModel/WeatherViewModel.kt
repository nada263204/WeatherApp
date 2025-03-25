package com.example.weatherapp.home.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.models.CurrentWeatherState
import com.example.weatherapp.data.models.ForecastWeatherState
import com.example.weatherapp.data.repo.LocationRepository
import com.example.weatherapp.data.repo.WeatherRepository
import com.example.weatherapp.setting.SettingsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

data class LocationData(
    val latitude: Double,
    val longitude: Double
)

class WeatherViewModel(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository,
    private val settingsViewModel: SettingsViewModel
) : ViewModel() {

    private val _location = MutableStateFlow<LocationData?>(null)
    val location: StateFlow<LocationData?> = _location.asStateFlow()

    private val _currentWeatherState =
        MutableStateFlow<CurrentWeatherState>(CurrentWeatherState.Loading)
    val currentWeatherState = _currentWeatherState.asStateFlow()

    private val _forecastWeatherState =
        MutableStateFlow<ForecastWeatherState>(ForecastWeatherState.Loading)
    val forecastWeatherState = _forecastWeatherState.asStateFlow()

    private val _temperatureUnit = MutableStateFlow("°C")
    val temperatureUnit: StateFlow<String> = _temperatureUnit.asStateFlow()
    var unitPreference = mutableStateOf("metric")

    private val _isUserSelectedLocation = MutableStateFlow(false)
    val isUserSelectedLocation: StateFlow<Boolean> = _isUserSelectedLocation.asStateFlow()

    init {
        fetchLastKnownLocation()
        observeLocationUpdates()
        observeSettingsChanges()
    }

    private fun observeSettingsChanges() {
        viewModelScope.launch {
            settingsViewModel.selectedTemperatureUnit.collectLatest { unit ->
                _temperatureUnit.value = when (unit) {
                    "Kelvin" -> "K"
                    "Fahrenheit" -> "°F"
                    else -> "°C"
                }

                _location.value?.let { loc ->
                    fetchWeatherData(loc.latitude, loc.longitude)
                }
            }
        }
    }


    private fun observeLocationUpdates() {
        viewModelScope.launch {
            locationRepository.locationLiveData.asFlow().collectLatest { location ->
                if (!_isUserSelectedLocation.value) {
                    location?.let {
                        val locationData = LocationData(it.latitude, it.longitude)
                        _location.value = locationData
                        fetchWeatherData(locationData.latitude, locationData.longitude)
                    }
                }
            }
        }
    }


    private fun fetchLastKnownLocation() {
        viewModelScope.launch {
            locationRepository.locationLiveData.asFlow().collectLatest { location ->
                location?.let {
                    val locationData = LocationData(it.latitude, it.longitude)
                    _location.value = locationData
                    fetchForecastWeather(locationData.latitude, locationData.longitude)
                }
            }
        }
    }


    private fun fetchWeatherData(lat: Double, lon: Double) {
        fetchCurrentWeather(lat, lon)
        fetchForecastWeather(lat, lon)
    }

    fun fetchCurrentWeather(lat: Double, lon: Double) {
        val lang = Locale.getDefault().language
        val units = when (settingsViewModel.selectedTemperatureUnit.value) {
            "Kelvin" -> "standard"
            "Fahrenheit" -> "imperial"
            else -> "metric"
        }

        Log.i("Find Language", "fetchCurrentWeather: $units")
        viewModelScope.launch {
            try {
                weatherRepository.getCurrentWeather(lat, lon, lang, units, isOnline = true)
                    .collectLatest { response ->
                        _currentWeatherState.value = response?.let {
                            CurrentWeatherState.Success(it)
                        } ?: CurrentWeatherState.Failure(Exception("Weather data unavailable"))
                    }
            } catch (e: Exception) {
                _currentWeatherState.value = CurrentWeatherState.Failure(e)
                Log.e("WeatherViewModel", "fetchCurrentWeather failed", e)
            }
        }
    }

    fun fetchForecastWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            val lang = Locale.getDefault().language
            val units = when (settingsViewModel.selectedTemperatureUnit.value) {
                "Kelvin" -> "standard"
                "Fahrenheit" -> "imperial"
                else -> "metric"
            }
            try {
                weatherRepository.getForecastWeather(lat, lon, lang, units, isOnline = true)
                    .collectLatest { response ->
                        val dailyForecast = response?.list
                            ?.groupBy { it.dt_txt.substring(0, 10) }
                            ?.map { (_, items) -> items.first() }

                        _forecastWeatherState.value = dailyForecast?.let {
                            ForecastWeatherState.Success(response.copy(list = it))
                        } ?: ForecastWeatherState.Failure(Exception("No data received"))
                    }
            } catch (e: Exception) {
                _forecastWeatherState.value = ForecastWeatherState.Failure(e)
                Log.e("WeatherViewModel", "fetchForecastWeather failed", e)
            }
        }
    }

    fun setUserSelectedLocation(lat: Double, lon: Double) {
        _isUserSelectedLocation.value = true
        _location.value = LocationData(lat, lon)
        fetchWeatherData(lat, lon)
    }
}

class WeatherViewModelFactory(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository,
    private val settingsViewModel: SettingsViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(weatherRepository, locationRepository, settingsViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

