package com.example.weatherapp.data.repo

import com.example.weatherapp.data.local.FavoritePlace
import com.example.weatherapp.data.models.Response5days3hours
import com.example.weatherapp.data.models.ResponseCurrentWeather
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getCurrentWeather(lat: Double, lon: Double, lang: String ,units:String ,isOnline: Boolean): Flow<ResponseCurrentWeather?>
    suspend fun getForecastWeather(lat: Double, lon: Double,lang: String ,units:String ,isOnline: Boolean): Flow<Response5days3hours?>

    fun getFavoritePlaces(): Flow<List<FavoritePlace>>
    suspend fun saveFavoritePlace(place: FavoritePlace)
    suspend fun deleteFavoritePlace(place: FavoritePlace)


}
