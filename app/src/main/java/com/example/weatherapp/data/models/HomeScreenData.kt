package com.example.weatherapp.data.models


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.weatherapp.data.models.*
import com.example.weatherapp.utiles.WeatherTypeConverters

@Entity(tableName = "home_screen_data")
@TypeConverters(WeatherTypeConverters::class)
data class HomeScreenData(
    @PrimaryKey val cityName: String,
    val coord: Coord,
    val main: Main,
    val clouds: Clouds,
    val weather: List<WeatherItem>,
    val wind: Wind,
    val forecast: List<ListItem>,
    val city: City
)
