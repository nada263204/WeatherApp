package com.example.weatherapp.data.local

import com.example.weatherapp.data.models.HomeScreenData
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    fun getAllFavoritePlaces(): Flow<List<FavoritePlace>>
    fun getFavoritePlaceByName(cityName: String): Flow<FavoritePlace?>
    suspend fun insertFavoritePlace(place: FavoritePlace)
    suspend fun deleteFavoritePlace(place: FavoritePlace)

    suspend fun insertNotification(notification: NotificationEntity)
    fun getAllNotifications(): Flow<List<NotificationEntity>>
    suspend fun deleteNotificationById(id: Int)
    suspend fun deleteExpiredNotifications(currentTime: String)

    fun getHomeScreenData(): Flow<List<HomeScreenData>>
    suspend fun insertHomeScreenData(data: HomeScreenData)
    suspend fun clearHomeScreenData()
}
