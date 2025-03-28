package com.example.weatherapp.setting

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.models.WeatherData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.core.content.edit
import androidx.lifecycle.asFlow
import com.example.weatherapp.data.repo.LocationRepository
import com.example.weatherapp.home.viewModel.LocationData
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.roundToInt

class SettingsViewModel(private val context: Context, private val locationRepository: LocationRepository) : ViewModel() {
    private val sharedPreferences = context.getSharedPreferences("WeatherPreferences", Context.MODE_PRIVATE)
    private val _selectedLocation = MutableStateFlow(getSavedLocationMethod(context))
    val selectedLocation: StateFlow<String> = _selectedLocation.asStateFlow()

    private val _currentLocation = MutableStateFlow<LocationData?>(null)
    val currentLocation: StateFlow<LocationData?> = _currentLocation.asStateFlow()

    private val _selectedTemperatureUnit = MutableStateFlow(getSavedTemperatureUnit(context))
    val selectedTemperatureUnit: StateFlow<String> = _selectedTemperatureUnit

    private val _selectedWindSpeedUnit = MutableStateFlow(getSavedWindSpeedUnit(context))
    val selectedWindSpeedUnit: StateFlow<String> = _selectedWindSpeedUnit

    private val _selectedLanguage = MutableStateFlow(LanguageChangeHelper.getSavedLanguage(context))
    val selectedLanguage: StateFlow<String> = _selectedLanguage

    private val _weatherData = MutableStateFlow<WeatherData?>(null)
    val weatherData: StateFlow<WeatherData?> = _weatherData

    init {
        if (_selectedLocation.value == "GPS") {
            fetchCurrentLocation()
        }
        _selectedLocation.value = getSavedLocationMethod(context)
        if (_selectedLocation.value == "GPS") {
            fetchCurrentLocation()
        }
    }

    private fun getSavedLocationMethod(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(LOCATION_METHOD_KEY, "GPS") ?: "GPS"
    }

    private fun saveLocationMethod(method: String) {
        sharedPreferences.edit {
            putString("location_method", method)
        }
    }


//    fun getFormattedTemperature(tempInCelsius: Double): String {
//        return when (_selectedTemperatureUnit.value) {
//            "Kelvin" -> "${(tempInCelsius + 273.15).roundToInt()} K"
//            "Fahrenheit" -> "${((tempInCelsius * 9 / 5) + 32).roundToInt()}°F"
//            else -> "${tempInCelsius.roundToInt()}°C"
//        }
//    }
//
//    fun getFormattedWindSpeed(windInKmH: Double): String {
//        return when (_selectedWindSpeedUnit.value) {
//            "mph" -> "${(windInKmH * 0.621371).roundToInt()} mph"
//            else -> "${windInKmH.roundToInt()} km/h"
//        }
//    }

    fun updateLocation(option: String) {
        viewModelScope.launch {
            sharedPreferences.edit().putString("location_method", option).apply()
            _selectedLocation.emit(option)
        }
    }

    fun updateTemperatureUnit(unit: String) {
        viewModelScope.launch {
            _selectedTemperatureUnit.emit(unit)
            saveTemperatureUnit(context, unit)

            val defaultWindSpeedUnit = if (unit == "Kelvin") "mph" else "m/s"
            _selectedWindSpeedUnit.emit(defaultWindSpeedUnit)
            saveWindSpeedUnit(context, defaultWindSpeedUnit)
        }
    }

    fun updateWindSpeedUnit(unit: String) {
        viewModelScope.launch {
            _selectedWindSpeedUnit.emit(unit)
            saveWindSpeedUnit(context, unit)

            if (unit == "mph") {
                _selectedTemperatureUnit.emit("Kelvin")
                saveTemperatureUnit(context, "Kelvin")
            }
        }
    }

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            val languageCode = if (language == "Arabic") "ar" else "en"
            _selectedLanguage.emit(languageCode)
            LanguageChangeHelper.changeLanguage(context, languageCode)
        }
    }

    private fun fetchCurrentLocation() {
        viewModelScope.launch {
            locationRepository.locationLiveData.asFlow().collectLatest { location ->
                location?.let {
                    val locationData = LocationData(it.latitude, it.longitude)
                    _currentLocation.value = locationData


                }
            }
        }
    }

    fun saveLocationData(lat: Double, lon: Double) {
        sharedPreferences.edit {
            putFloat("saved_lat", lat.toFloat())
            putFloat("saved_lon", lon.toFloat())
        }
    }


    fun getSavedLocationData(): LocationData? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lat = prefs.getFloat("saved_lat", Float.MIN_VALUE)
        val lon = prefs.getFloat("saved_lon", Float.MIN_VALUE)
        return if (lat != Float.MIN_VALUE && lon != Float.MIN_VALUE) {
            LocationData(lat.toDouble(), lon.toDouble())
        } else {
            null
        }
    }


    companion object {
        private const val PREFS_NAME = "weather_prefs"
        private const val TEMP_UNIT_KEY = "temperature_unit"
        private const val WIND_SPEED_UNIT_KEY = "wind_speed_unit"
        private const val LOCATION_METHOD_KEY = "location_method"

        private fun getSavedTemperatureUnit(context: Context): String {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(TEMP_UNIT_KEY, "Celsius") ?: "Celsius"
        }

        fun saveTemperatureUnit(context: Context, unit: String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit { putString(TEMP_UNIT_KEY, unit) }
        }

        private fun getSavedWindSpeedUnit(context: Context): String {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(WIND_SPEED_UNIT_KEY, "m/s") ?: "m/s"
        }

        private fun saveWindSpeedUnit(context: Context, unit: String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit { putString(WIND_SPEED_UNIT_KEY, unit) }
        }

        private fun getSavedLocationMethod(context: Context): String {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(LOCATION_METHOD_KEY, "GPS") ?: "GPS"
        }

        private fun saveLocationMethod(context: Context, method: String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit { putString(LOCATION_METHOD_KEY, method) }
        }
    }
}



class SettingsViewModelFactory(private val context: Context, private val locationRepository: LocationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(context, locationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
