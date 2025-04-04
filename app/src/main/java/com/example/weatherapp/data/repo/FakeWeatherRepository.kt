package com.example.weatherapp.data.repo

import com.example.weatherapp.data.local.FavoritePlace
import com.example.weatherapp.data.models.HomeScreenData
import com.example.weatherapp.data.models.Response5days3hours
import com.example.weatherapp.data.models.ResponseCurrentWeather
import kotlinx.coroutines.flow.Flow

class FakeWeatherRepository: WeatherRepository {
    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        lang: String,
        units: String
    ): Flow<ResponseCurrentWeather?> {
        TODO("Not yet implemented")
    }

    override suspend fun getForecastWeather(
        lat: Double,
        lon: Double,
        lang: String,
        units: String
    ): Flow<Response5days3hours?> {
        TODO("Not yet implemented")
    }

    override fun getFavoritePlaces(): Flow<List<FavoritePlace>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveFavoritePlace(place: FavoritePlace) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFavoritePlace(place: FavoritePlace) {
        TODO("Not yet implemented")
    }

    override fun getHomeScreenData(): Flow<List<HomeScreenData>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveHomeScreenData(homeScreenData: HomeScreenData) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteHomeScreenData() {
        TODO("Not yet implemented")
    }
}