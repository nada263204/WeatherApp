package com.example.weatherapp.utiles

import androidx.room.TypeConverter
import com.example.weatherapp.data.models.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WeatherTypeConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromWeatherList(value: List<WeatherItem>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toWeatherList(value: String?): List<WeatherItem>? {
        val type = object : TypeToken<List<WeatherItem>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromListItemList(value: List<ListItem>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toListItemList(value: String?): List<ListItem>? {
        val type = object : TypeToken<List<ListItem>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromCoord(value: Coord?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toCoord(value: String?): Coord? {
        return gson.fromJson(value, Coord::class.java)
    }

    @TypeConverter
    fun fromMain(value: Main?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMain(value: String?): Main? {
        return gson.fromJson(value, Main::class.java)
    }

    @TypeConverter
    fun fromClouds(value: Clouds?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toClouds(value: String?): Clouds? {
        return gson.fromJson(value, Clouds::class.java)
    }

    @TypeConverter
    fun fromWind(value: Wind?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toWind(value: String?): Wind? {
        return gson.fromJson(value, Wind::class.java)
    }

    @TypeConverter
    fun fromCity(value: City?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toCity(value: String?): City? {
        return gson.fromJson(value, City::class.java)
    }
}
