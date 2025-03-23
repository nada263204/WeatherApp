package com.example.weatherapp.data.repo

import com.example.weatherapp.data.models.Response5days3hours
import com.example.weatherapp.data.models.ResponseCurrentWeather
import com.example.weatherapp.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class WeatherRepositoryImpl(private val remoteDataSource: RemoteDataSource) : WeatherRepository {

    override suspend fun getCurrentWeather(lat: Double, lon: Double, isOnline: Boolean): Flow<ResponseCurrentWeather?> {
        return flow {
            emit(remoteDataSource.getCurrentWeather(lat, lon).firstOrNull())
        }.catch { e ->
            emit(null)
        }
    }

    override suspend fun getForecastWeather(lat: Double, lon: Double,isOnline: Boolean): Flow<Response5days3hours?> {
        return flow {
            emit(remoteDataSource.getForecastWeather(lat, lon).firstOrNull())
        }.catch { e ->
            emit(null)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: WeatherRepositoryImpl? = null

        fun getInstance(remoteDataSource: RemoteDataSource): WeatherRepositoryImpl {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: WeatherRepositoryImpl(remoteDataSource).also { INSTANCE = it }
            }
        }
    }
}
