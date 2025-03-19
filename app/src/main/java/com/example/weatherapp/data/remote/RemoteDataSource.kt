package com.example.weatherapp.data.remote

import com.example.weatherapp.data.models.Response5days3hours
import com.example.weatherapp.data.models.ResponseCurrentWeather

interface RemoteDataSource {
    suspend fun getCurrentWeather(): ResponseCurrentWeather?
    suspend fun getForecastWeather():Response5days3hours?
}