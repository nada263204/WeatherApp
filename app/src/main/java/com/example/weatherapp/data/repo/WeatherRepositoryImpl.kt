package com.example.weatherapp.data.repo

import com.example.weatherapp.data.local.FavoritePlace
import com.example.weatherapp.data.local.LocalDataSource
import com.example.weatherapp.data.models.Response5days3hours
import com.example.weatherapp.data.models.ResponseCurrentWeather
import com.example.weatherapp.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

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
            if (isOnline) {
                val response = remoteDataSource.getCurrentWeather(lat, lon, lang, units).firstOrNull()
                emit(response)
            } else {
                // Fetch from the favorite places if offline
                val favoritePlaces = localDataSource.getAllFavoritePlaces()
                emit(favoritePlaces.map { it.find { place -> place.coord.lat == lat && place.coord.lon == lon }?.let { toCurrentWeather(it) } }.firstOrNull())
            }
        }.catch { emit(null) }
    }

    override suspend fun getForecastWeather(
        lat: Double,
        lon: Double,
        lang: String,
        units: String,
        isOnline: Boolean
    ): Flow<Response5days3hours?> {
        return flow {
            if (isOnline) {
                val response = remoteDataSource.getForecastWeather(lat, lon, lang, units).firstOrNull()
                emit(response)
            } else {
                val favoritePlaces = localDataSource.getAllFavoritePlaces()
                emit(favoritePlaces.map { it.find { place -> place.coord.lat == lat && place.coord.lon == lon }?.let { toForecastWeather(it) } }.firstOrNull())
            }
        }.catch { emit(null) }
    }

    override fun getFavoritePlaces(): Flow<List<FavoritePlace>> {
        return localDataSource.getAllFavoritePlaces()
    }

    override suspend fun saveFavoritePlace(place: FavoritePlace) {
        localDataSource.insertFavoritePlace(place)
    }

    override suspend fun deleteFavoritePlace(place: FavoritePlace) {
        localDataSource.deleteFavoritePlace(place)
    }

    private fun toCurrentWeather(favoritePlace: FavoritePlace): ResponseCurrentWeather {
        return ResponseCurrentWeather(
            coord = favoritePlace.coord,
            main = favoritePlace.main,
            clouds = favoritePlace.clouds,
            weather = favoritePlace.weather,
            wind = favoritePlace.wind,
            name = favoritePlace.cityName
        )
    }

    private fun toForecastWeather(favoritePlace: FavoritePlace): Response5days3hours {
        return Response5days3hours(
            list = favoritePlace.forecast,
            city = favoritePlace.city
        )
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
