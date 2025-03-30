package com.example.weatherapp.data.local


import com.example.weatherapp.data.models.HomeScreenData
import com.example.weatherapp.notifications.NotificationDao
import com.example.weatherapp.notifications.NotificationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LocalDataSourceImpl(
    private val favoritePlaceDao: FavoritePlaceDao,
    private val notificationDao: NotificationDao,
    private val homeScreenDao: HomeScreenDao,
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

    override fun getHomeScreenData(): Flow<List<HomeScreenData>> {
        return homeScreenDao.getHomeScreenData()
    }

    override suspend fun insertHomeScreenData(data: HomeScreenData) {
        homeScreenDao.insertHomeScreenData(data)
    }

    override suspend fun clearHomeScreenData() {
        homeScreenDao.clearHomeScreenData()
    }

}
