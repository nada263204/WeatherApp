package com.example.weatherapp.home.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.models.CurrentWeatherState
import com.example.weatherapp.data.models.ForecastWeatherState
import com.example.weatherapp.data.repo.LocationRepository
import com.example.weatherapp.data.repo.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow

data class LocationData(
    val latitude: Double,
    val longitude: Double
)

class WeatherViewModel(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _location = MutableStateFlow<LocationData?>(null)
    val location: StateFlow<LocationData?> = _location.asStateFlow()

    private val _currentWeatherState =
        MutableStateFlow<CurrentWeatherState>(CurrentWeatherState.Loading)
    val currentWeatherState = _currentWeatherState.asStateFlow()

    private val _forecastWeatherState =
        MutableStateFlow<ForecastWeatherState>(ForecastWeatherState.Loading)
    val forecastWeatherState = _forecastWeatherState.asStateFlow()

    init {
        fetchLastKnownLocation()
        observeLocationUpdates()
    }

    private fun observeLocationUpdates() {
        viewModelScope.launch {
            locationRepository.locationLiveData.asFlow().collectLatest { location ->
                location?.let {
                    val locationData = LocationData(it.latitude, it.longitude)
                    _location.value = locationData
                    fetchWeatherData(locationData.latitude, locationData.longitude)
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
        viewModelScope.launch {
            try {
                weatherRepository.getCurrentWeather(lat, lon, isOnline = true)
                    .collectLatest { response ->
                        _currentWeatherState.value = response?.let {
                            CurrentWeatherState.Success(it)
                        } ?: CurrentWeatherState.Failure(Exception("No data received"))
                    }
            } catch (e: Exception) {
                _currentWeatherState.value = CurrentWeatherState.Failure(e)
                Log.e("WeatherViewModel", "fetchCurrentWeather failed", e)
            }
        }
    }

    fun fetchForecastWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                weatherRepository.getForecastWeather(lat, lon, isOnline = true)
                    .collectLatest { response ->
                        _forecastWeatherState.value = response?.let {
                            ForecastWeatherState.Success(it)
                        } ?: ForecastWeatherState.Failure(Exception("No data received"))
                    }
            } catch (e: Exception) {
                _forecastWeatherState.value = ForecastWeatherState.Failure(e)
                Log.e("WeatherViewModel", "fetchForecastWeather failed", e)
            }
        }
    }
}

class WeatherViewModelFactory(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(weatherRepository, locationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
