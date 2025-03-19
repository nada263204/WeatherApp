package com.example.weatherapp.data.repo

import android.util.Log
import com.example.weatherapp.data.models.Response5days3hours
import com.example.weatherapp.data.models.ResponseCurrentWeather
import com.example.weatherapp.data.remote.RemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepositoryImpl private constructor(private val remoteDataSource : RemoteDataSource): WeatherRepository {

    override suspend fun getCurrentWeather(isOnline: Boolean): ResponseCurrentWeather? {
        return remoteDataSource.getCurrentWeather()
    }

    override suspend fun getForecastWeather(isOnline: Boolean): Response5days3hours? {
        return remoteDataSource.getForecastWeather()
    }

    companion object{
        private var INSTANCE : WeatherRepositoryImpl ?= null
        fun getInstance(remoteDataSource: RemoteDataSource): WeatherRepository{
            return INSTANCE ?: synchronized (this){
                val temp = WeatherRepositoryImpl(remoteDataSource)
                INSTANCE=temp
                temp
            }
        }
    }


}
