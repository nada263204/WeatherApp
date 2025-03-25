package com.example.weatherapp.data.local

import com.example.weatherapp.data.models.FavoritePlace
import kotlinx.coroutines.flow.Flow

class LocalDataSourceImpl(private val favoritePlaceDao: FavoritePlaceDao) : LocalDataSource {

    override fun getAllFavoritePlaces(): Flow<List<FavoritePlace>> {
        return favoritePlaceDao.getAllFavoritePlaces()
    }

    override suspend fun insertFavoritePlace(place: FavoritePlace) {
        favoritePlaceDao.insertFavoritePlace(place)
    }

    override suspend fun deleteFavoritePlace(place: FavoritePlace) {
        favoritePlaceDao.deleteFavoritePlace(place)
    }
}
