package com.example.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritePlaceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoritePlace(place: FavoritePlace)

    @Query("SELECT * FROM favorite_places")
    fun getAllFavoritePlaces(): Flow<List<FavoritePlace>>

    @Delete
    suspend fun deleteFavoritePlace(place: FavoritePlace)

    @Query("SELECT * FROM favorite_places WHERE cityName = :cityName LIMIT 1")
    fun getFavoritePlaceByName(cityName: String): Flow<FavoritePlace?>
}
