package com.example.weatherapp.data.repo

import android.content.Context
import android.util.Log
import com.example.weatherapp.data.local.FavoritePlace
import com.example.weatherapp.data.local.LocalDataSource
import com.example.weatherapp.data.models.HomeScreenData
import com.example.weatherapp.data.models.Response5days3hours
import com.example.weatherapp.data.models.ResponseCurrentWeather
import com.example.weatherapp.data.remote.RemoteDataSource
import com.example.weatherapp.utils.NetworkUtils
import kotlinx.coroutines.flow.*

class WeatherRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val context: Context
) : WeatherRepository {

    private val isOnline: Boolean
        get() = NetworkUtils.isNetworkAvailable(context)

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        lang: String,
        units: String,
    ): Flow<ResponseCurrentWeather?> {
        return flow {
            if (isOnline) {
                val response = remoteDataSource.getCurrentWeather(lat, lon, lang, units).firstOrNull()
                emit(response)
            } else {
                val homeScreenDataList = localDataSource.getHomeScreenData().firstOrNull()
                val lastHomeData = homeScreenDataList?.lastOrNull()

                if (lastHomeData != null) {
                    emit(fromHomeDataToCurrentWeather(lastHomeData))
                }

                val favoritePlaces = localDataSource.getAllFavoritePlaces().firstOrNull()
                val matchingFavorite = favoritePlaces?.find { areCoordinatesEqual(it.coord.lat, lat) && areCoordinatesEqual(it.coord.lon, lon) }

                if (matchingFavorite != null) {
                    emit(toCurrentWeather(matchingFavorite))
                }
            }
        }.catch { e ->
            emit(null)
        }
    }

    override suspend fun getForecastWeather(
        lat: Double,
        lon: Double,
        lang: String,
        units: String
    ): Flow<Response5days3hours?> {
        return flow {
            if (isOnline) {
                val response = remoteDataSource.getForecastWeather(lat, lon, lang, units).firstOrNull()
                emit(response)
            } else {
                val homeScreenDataList = localDataSource.getHomeScreenData().firstOrNull()
                val lastHomeData = homeScreenDataList?.lastOrNull()

                if (lastHomeData != null) {
                    emit(fromHomeDataToForecastWeather(lastHomeData))
                }

                val favoritePlace = localDataSource.getAllFavoritePlaces().firstOrNull()
                    ?.find { areCoordinatesEqual(it.coord.lat, lat) && areCoordinatesEqual(it.coord.lon, lon) }

                if (favoritePlace != null) {
                    emit(toForecastWeather(favoritePlace))
                }
            }
        }.catch { e ->
            emit(null)
        }
    }

    private fun areCoordinatesEqual(a: Double, b: Double, tolerance: Double = 0.0001): Boolean {
        return kotlin.math.abs(a - b) < tolerance
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

    override fun getHomeScreenData(): Flow<List<HomeScreenData>> {
        return localDataSource.getHomeScreenData()
    }

    override suspend fun saveHomeScreenData(homeScreenData: HomeScreenData) {
        localDataSource.insertHomeScreenData(homeScreenData)
    }

    override suspend fun deleteHomeScreenData() {
        localDataSource.clearHomeScreenData()
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

    private fun fromHomeDataToCurrentWeather(homeData: HomeScreenData): ResponseCurrentWeather {
        return ResponseCurrentWeather(
            coord = homeData.coord,
            main = homeData.main,
            clouds = homeData.clouds,
            weather = homeData.weather,
            wind = homeData.wind,
            name = homeData.cityName
        )
    }

    private fun fromHomeDataToForecastWeather(homeData: HomeScreenData): Response5days3hours {
        return Response5days3hours(
            list = homeData.forecast,
            city = homeData.city
        )
    }

    companion object {
        @Volatile
        private var INSTANCE: WeatherRepositoryImpl? = null
        fun getInstance(remoteDataSource: RemoteDataSource, localDataSource: LocalDataSource, context: Context): WeatherRepositoryImpl {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: WeatherRepositoryImpl(remoteDataSource, localDataSource, context).also { INSTANCE = it }
            }
        }
    }
}
