package com.example.weatherapp.data.models

data class WeatherData(
    val currentWeather: ResponseCurrentWeather?,
    val forecastWeather: Response5days3hours?
)
