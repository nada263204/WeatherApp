package com.example.weatherapp.data.local

import com.example.weatherapp.data.models.HomeScreenData
import com.example.weatherapp.notifications.NotificationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeLocalDataSource : LocalDataSource {

    private val favoritePlacesList = mutableListOf<FavoritePlace>()
    private val favoritePlacesFlow = MutableStateFlow<List<FavoritePlace>>(emptyList())

    override fun getAllFavoritePlaces(): Flow<List<FavoritePlace>> {
        return favoritePlacesFlow.asStateFlow()
    }

    override suspend fun insertFavoritePlace(place: FavoritePlace) {
        favoritePlacesList.add(place)
        favoritePlacesFlow.value = favoritePlacesList.toList()
    }

    override fun getFavoritePlaceByName(cityName: String): Flow<FavoritePlace?> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFavoritePlace(place: FavoritePlace) {
        favoritePlacesList.remove(place)
        favoritePlacesFlow.value = favoritePlacesList.toList()
    }

    override suspend fun insertNotification(notification: NotificationEntity) {
        TODO("Not yet implemented")
    }

    override fun getAllNotifications(): Flow<List<NotificationEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteNotificationById(id: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteExpiredNotifications(currentTime: String) {
        TODO("Not yet implemented")
    }

    override fun getHomeScreenData(): Flow<List<HomeScreenData>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertHomeScreenData(data: HomeScreenData) {
        TODO("Not yet implemented")
    }

    override suspend fun clearHomeScreenData() {
        TODO("Not yet implemented")
    }
}
