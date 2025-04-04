package com.example.weatherapp.data.remote

import com.example.weatherapp.data.models.Response5days3hours
import com.example.weatherapp.data.models.ResponseCurrentWeather
import kotlinx.coroutines.flow.Flow

class FakeRemoteDataSource : RemoteDataSource {
    override fun getCurrentWeather(
        lat: Double,
        lon: Double,
        lang: String,
        units: String
    ): Flow<ResponseCurrentWeather?> {
        TODO("Not yet implemented")
    }

    override fun getForecastWeather(
        lat: Double,
        lon: Double,
        lang: String,
        units: String
    ): Flow<Response5days3hours?> {
        TODO("Not yet implemented")
    }
}