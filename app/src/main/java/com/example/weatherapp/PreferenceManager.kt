package com.example.weatherapp

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "weather_pref"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
    }

    fun saveTemperatureUnit(unit: String) {
        sharedPreferences.edit().putString("temperature_unit", unit).apply()
    }

    fun getTemperatureUnit(): String {
        return sharedPreferences.getString("temperature_unit", "Celsius") ?: "Celsius"
    }

    fun saveWindSpeedUnit(unit: String) {
        sharedPreferences.edit().putString("wind_speed_unit", unit).apply()
    }

    fun getWindSpeedUnit(): String {
        return sharedPreferences.getString("wind_speed_unit", "m/s") ?: "m/s"
    }

    fun saveLanguage(language: String) {
        sharedPreferences.edit().putString("language", language).apply()
    }

    fun getLanguage(): String {
        return sharedPreferences.getString("language", "en") ?: "en"
    }

    fun saveLocation(latitude: Double, longitude: Double) {
        sharedPreferences.edit()
            .putFloat(KEY_LATITUDE, latitude.toFloat())
            .putFloat(KEY_LONGITUDE, longitude.toFloat())
            .apply()
    }

    fun getLocation(): Pair<Double, Double>? {
        val lat = sharedPreferences.getFloat(KEY_LATITUDE, Float.NaN)
        val lon = sharedPreferences.getFloat(KEY_LONGITUDE, Float.NaN)

        return if (!lat.isNaN() && !lon.isNaN()) {
            Pair(lat.toDouble(), lon.toDouble())
        } else {
            null
        }
    }
}