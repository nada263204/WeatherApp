package com.example.weatherapp.utiles

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "weather_pref"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
        private const val KEY_TEMPERATURE_UNIT = "temperature_unit"
        private const val KEY_WIND_SPEED_UNIT = "wind_speed_unit"
        private const val KEY_LANGUAGE = "language"

        private const val KEY_TEMPERATURE_UNIT_CODE = "temperature_unit_code"
        private const val KEY_WIND_SPEED_UNIT_CODE = "wind_speed_unit_code"
    }

    fun saveTemperatureUnit(unit: String) {
        sharedPreferences.edit().putString(KEY_TEMPERATURE_UNIT, unit).apply()
    }

    fun getTemperatureUnit(): String {
        return sharedPreferences.getString(KEY_TEMPERATURE_UNIT, "Celsius") ?: "Celsius"
    }

    fun saveWindSpeedUnit(unit: String) {
        sharedPreferences.edit().putString(KEY_WIND_SPEED_UNIT, unit).apply()
    }

    fun getWindSpeedUnit(): String {
        return sharedPreferences.getString(KEY_WIND_SPEED_UNIT, "m/s") ?: "m/s"
    }

    fun saveLanguage(language: String) {
        sharedPreferences.edit().putString(KEY_LANGUAGE, language).apply()
    }

    fun getLanguage(): String {
        return sharedPreferences.getString(KEY_LANGUAGE, "en") ?: "en"
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

    fun saveTemperatureUnitCode(unitCode: String) {
        sharedPreferences.edit().putString(KEY_TEMPERATURE_UNIT_CODE, unitCode).apply()
    }

    fun getTemperatureUnitCode(): String {
        return sharedPreferences.getString(KEY_TEMPERATURE_UNIT_CODE, "C") ?: "C"
    }

    fun saveWindSpeedUnitCode(unitCode: String) {
        sharedPreferences.edit().putString(KEY_WIND_SPEED_UNIT_CODE, unitCode).apply()
    }

    fun getWindSpeedUnitCode(): String {
        return sharedPreferences.getString(KEY_WIND_SPEED_UNIT_CODE, "mps") ?: "mps"
    }




    fun clearPreferences() {
        sharedPreferences.edit().clear().apply()
    }
}