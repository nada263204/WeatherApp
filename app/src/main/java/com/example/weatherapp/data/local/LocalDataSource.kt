package com.example.weatherapp.data.local

import com.example.weatherapp.data.models.FavoritePlace
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    fun getAllFavoritePlaces(): Flow<List<FavoritePlace>>
    suspend fun insertFavoritePlace(place: FavoritePlace)
    suspend fun deleteFavoritePlace(place: FavoritePlace)
}
