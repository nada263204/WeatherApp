package com.example.weatherapp.data.local

import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    fun getAllFavoritePlaces(): Flow<List<FavoritePlace>>
    fun getFavoritePlaceByName(cityName: String): Flow<FavoritePlace?>
    suspend fun insertFavoritePlace(place: FavoritePlace)
    suspend fun deleteFavoritePlace(place: FavoritePlace)
}
