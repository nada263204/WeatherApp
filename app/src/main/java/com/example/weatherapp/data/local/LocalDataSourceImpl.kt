package com.example.weatherapp.data.local


import com.example.weatherapp.notifications.NotificationDao
import com.example.weatherapp.notifications.NotificationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LocalDataSourceImpl(
    private val favoritePlaceDao: FavoritePlaceDao,
    private val notificationDao: NotificationDao
) : LocalDataSource {

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

    override suspend fun insertNotification(notification: NotificationEntity) {
        notificationDao.insertNotification(notification)
    }

    override fun getAllNotifications(): Flow<List<NotificationEntity>> = notificationDao.getAllNotifications()

    override suspend fun deleteNotificationById(id: Int) {
        notificationDao.deleteNotificationById(id)
    }

    override suspend fun deleteExpiredNotifications(currentTime: String) {
        notificationDao.deleteExpiredNotifications(currentTime)
    }
}
