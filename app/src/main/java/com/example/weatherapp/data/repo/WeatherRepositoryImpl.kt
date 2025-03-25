package com.example.weatherapp.data.repo

import com.example.weatherapp.data.local.LocalDataSource
import com.example.weatherapp.data.models.FavoritePlace
import com.example.weatherapp.data.models.Response5days3hours
import com.example.weatherapp.data.models.ResponseCurrentWeather
import com.example.weatherapp.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class WeatherRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : WeatherRepository {

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        lang: String,
        units: String,
        isOnline: Boolean
    ): Flow<ResponseCurrentWeather?> {
        return flow {
            emit(remoteDataSource.getCurrentWeather(lat, lon, lang, units).firstOrNull())
        }.catch { e ->
            emit(null)
        }
    }

    override suspend fun getForecastWeather(
        lat: Double,
        lon: Double,
        lang: String,
        units: String,
        isOnline: Boolean
    ): Flow<Response5days3hours?> {
        return flow {
            emit(remoteDataSource.getForecastWeather(lat, lon, lang, units).firstOrNull())
        }.catch { e ->
            emit(null)
        }
    }

    override fun getAllFavoritePlaces(): Flow<List<FavoritePlace>> {
        return localDataSource.getAllFavoritePlaces()
    }

    override suspend fun insertFavoritePlace(place: FavoritePlace) {
        localDataSource.insertFavoritePlace(place)
    }

    override suspend fun deleteFavoritePlace(place: FavoritePlace) {
        localDataSource.deleteFavoritePlace(place)
    }

    companion object {
        @Volatile
        private var INSTANCE: WeatherRepositoryImpl? = null

        fun getInstance(remoteDataSource: RemoteDataSource, localDataSource: LocalDataSource): WeatherRepositoryImpl {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: WeatherRepositoryImpl(remoteDataSource, localDataSource).also { INSTANCE = it }
            }
        }
    }
}
