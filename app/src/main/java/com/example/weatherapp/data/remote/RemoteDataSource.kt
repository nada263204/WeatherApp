package com.example.weatherapp.data.remote

import com.example.weatherapp.data.models.Response5days3hours
import com.example.weatherapp.data.models.ResponseCurrentWeather
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RemoteDataSource {
    fun getCurrentWeather(lat: Double, lon: Double , lang: String): Flow<ResponseCurrentWeather?>
    fun getForecastWeather(lat: Double, lon: Double , lang: String ): Flow<Response5days3hours?>
}
