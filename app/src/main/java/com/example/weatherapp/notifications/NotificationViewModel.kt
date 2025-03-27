package com.example.weatherapp.notifications

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import java.text.SimpleDateFormat
import java.util.*

class NotificationViewModel(application: Application) : AndroidViewModel(application) {
    private val db = NotificationDatabase.getDatabase(application).notificationDao()

    private val _notifications = MutableStateFlow<List<NotificationEntity>>(emptyList())
    val notifications: StateFlow<List<NotificationEntity>> = _notifications

    init {
        observeNotifications()
    }
    private fun observeNotifications() {
        viewModelScope.launch {
            db.getAllNotifications()
                .collect { notificationList ->
                    _notifications.value = notificationList
                }
        }
    }

    fun addNotification(date: String, time: String) {
        viewModelScope.launch {
            val notification = NotificationEntity(date = date, time = time)
            db.insertNotification(notification)
            scheduleNotification(date, time)
        }
    }

    private fun scheduleNotification(date: String, time: String) {
        val dateTimeString = "$date $time"
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val notificationTime = sdf.parse(dateTimeString)?.time ?: return
        val currentTime = System.currentTimeMillis()
        val delay = notificationTime - currentTime

        if (delay > 0) {
            val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(workDataOf("notification_time" to time))
                .build()

            WorkManager.getInstance(getApplication()).enqueue(workRequest)
        }
    }

    fun deleteNotification(time: String) {
        viewModelScope.launch {
            db.deleteNotificationByTime(time)
        }
    }

}

