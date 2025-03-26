package com.example.weatherapp.data.local

import kotlinx.coroutines.flow.Flow

class LocalDataSourceImpl(private val favoritePlaceDao: FavoritePlaceDao) : LocalDataSource {

    override fun getAllFavoritePlaces(): Flow<List<FavoritePlace>> {
        return favoritePlaceDao.getAllFavoritePlaces()
    }

    override fun getFavoritePlaceByName(cityName: String): Flow<FavoritePlace?> {
        return favoritePlaceDao.getFavoritePlaceByName(cityName)
    }

    override suspend fun insertFavoritePlace(place: FavoritePlace) {
        favoritePlaceDao.insertFavoritePlace(place)
    }

    override suspend fun deleteFavoritePlace(place: FavoritePlace) {
        favoritePlaceDao.deleteFavoritePlace(place)
    }
}
