package com.example.weatherapp.data.repo

import com.example.weatherapp.data.models.Response5days3hours
import com.example.weatherapp.data.models.ResponseCurrentWeather

interface WeatherRepository {
    suspend fun getCurrentWeather(isOnline : Boolean): ResponseCurrentWeather?
    suspend fun getForecastWeather(isOnline: Boolean): Response5days3hours?
}