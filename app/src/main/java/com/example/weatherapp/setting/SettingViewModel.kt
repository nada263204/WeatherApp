package com.example.weatherapp.setting

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val context: Context) : ViewModel() {
    private val _selectedLocation = MutableStateFlow("GPS")
    val selectedLocation: StateFlow<String> = _selectedLocation

    private val _selectedTemperatureUnit = MutableStateFlow("Celsius")
    val selectedTemperatureUnit: StateFlow<String> = _selectedTemperatureUnit

    private val _selectedWindSpeedUnit = MutableStateFlow("m/s")
    val selectedWindSpeedUnit: StateFlow<String> = _selectedWindSpeedUnit

    private val _selectedLanguage = MutableStateFlow(LanguageChangeHelper.getSavedLanguage(context))
    val selectedLanguage: StateFlow<String> = _selectedLanguage

    fun updateLocation(location: String) {
        _selectedLocation.value = location
    }

    fun updateTemperatureUnit(unit: String) {
        _selectedTemperatureUnit.value = unit
    }

    fun updateWindSpeedUnit(unit: String) {
        _selectedWindSpeedUnit.value = unit
    }

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            val languageCode = if (language == "Arabic") "ar" else "en"
            _selectedLanguage.value = languageCode
            LanguageChangeHelper.changeLanguage(context, languageCode)
        }
    }
}

class SettingsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}