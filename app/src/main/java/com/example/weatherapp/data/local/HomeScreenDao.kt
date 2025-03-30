package com.example.weatherapp.data.local

import androidx.room.*
import com.example.weatherapp.data.models.HomeScreenData
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeScreenDao {

    @Query("SELECT * FROM home_screen_data")
    fun getHomeScreenData(): Flow<List<HomeScreenData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomeScreenData(homeScreenData: HomeScreenData)

    @Query("DELETE FROM home_screen_data WHERE cityName = :cityName")
    suspend fun deleteHomeScreenData(cityName: String)

    @Query("DELETE FROM home_screen_data")
    suspend fun clearHomeScreenData()
}
