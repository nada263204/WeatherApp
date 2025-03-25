package com.example.weatherapp.data.local

import androidx.room.*
import com.example.weatherapp.data.models.FavoritePlace
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritePlaceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoritePlace(place: FavoritePlace)

    @Delete
    suspend fun deleteFavoritePlace(place: FavoritePlace)

    @Query("SELECT * FROM favorite_places")
    fun getAllFavoritePlaces(): Flow<List<FavoritePlace>>
}
