package com.example.weatherapp.data.models

data class ResponseCurrentWeather(
    val coord: Coord,
    val main: Main,
    val clouds: Clouds,
    val weather: List<WeatherItem>,
    val name: String,
    val wind: Wind
)
data class Response5days3hours(
    val city: City,
    val list: List<ListItem>
)
data class ListItem(
    val dt_txt: String,
    val weather: List<WeatherItem>,
    val main: Main,
    val clouds: Clouds,
    val wind: Wind,
)
data class Coord(
    val lon:Double,
    val lat:Double
)
data class City(
    val name: String,
    val coord: Coord
)
data class Clouds(
    val all: Int
)
data class Main(
    val temp: Any,
    val tempMin: Any,
    val humidity: Int,
    val pressure: Int,
    val tempMax: Any
)
data class WeatherItem(
    val icon: String,
    val description: String,
    val main: String,
)
data class Wind(
    val speed: Any,
)