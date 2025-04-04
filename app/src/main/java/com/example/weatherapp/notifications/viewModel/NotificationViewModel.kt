package com.example.weatherapp.notifications.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.weatherapp.data.local.NotificationDatabase
import com.example.weatherapp.data.local.NotificationEntity
import com.example.weatherapp.notifications.view.NotificationWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class NotificationViewModel(application: Application) : AndroidViewModel(application) {
    private val db = NotificationDatabase.Companion.getDatabase(application).notificationDao()

    private val _notifications = MutableStateFlow<List<NotificationEntity>>(emptyList())
    val notifications: StateFlow<List<NotificationEntity>> = _notifications

    init {
        observeNotifications()
    }
    private fun observeNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            db.getAllNotifications()
                .collect { notificationList ->
                    _notifications.value = notificationList
                }
        }
    }

    fun addNotification(date: String, time: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val notification = NotificationEntity(date = date, time = time)
            db.insertNotification(notification)
            delay(100)
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
        viewModelScope.launch (Dispatchers.IO){
            db.deleteNotificationByTime(time)
        }
    }

}