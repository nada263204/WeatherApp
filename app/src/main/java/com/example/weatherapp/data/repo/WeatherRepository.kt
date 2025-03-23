package com.example.weatherapp.data.repo

import com.example.weatherapp.data.models.Response5days3hours
import com.example.weatherapp.data.models.ResponseCurrentWeather
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getCurrentWeather(lat: Double, lon: Double, lang: String ,isOnline: Boolean): Flow<ResponseCurrentWeather?>
    suspend fun getForecastWeather(lat: Double, lon: Double,lang: String ,isOnline: Boolean): Flow<Response5days3hours?>
}
