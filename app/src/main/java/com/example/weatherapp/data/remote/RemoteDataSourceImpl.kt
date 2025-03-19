package com.example.weatherapp.data.remote

import android.util.Log
import com.example.weatherapp.data.models.Response5days3hours
import com.example.weatherapp.data.models.ResponseCurrentWeather
import retrofit2.HttpException

class RemoteDataSourceImpl (private val services: ApiServices): RemoteDataSource {
    override suspend fun getCurrentWeather(): ResponseCurrentWeather? {
        return services.getCurrentWeather().body()
    }

    override suspend fun getForecastWeather(): Response5days3hours? {
        return services.get5days3hoursForecast().body()
    }

}