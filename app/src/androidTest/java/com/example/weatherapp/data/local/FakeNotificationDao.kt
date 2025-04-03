package com.example.weatherapp.data.local

import com.example.weatherapp.notifications.NotificationDao
import com.example.weatherapp.notifications.NotificationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeNotificationDao : NotificationDao {
    private val notifications = MutableStateFlow<List<NotificationEntity>>(emptyList())
    override suspend fun insertNotification(notification: NotificationEntity) {
        TODO("Not yet implemented")
    }

    override fun getAllNotifications(): Flow<List<NotificationEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteNotificationByTime(time: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteNotificationById(id: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteExpiredNotifications(currentTime: String) {
        TODO("Not yet implemented")
    }


}