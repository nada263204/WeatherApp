package com.example.weatherapp.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.weatherapp.data.local.AppDatabase
import com.example.weatherapp.data.local.LocalDataSourceImpl
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.data.remote.RetrofitClient
import com.example.weatherapp.data.repo.WeatherRepositoryImpl
import com.example.weatherapp.utiles.LocationUtils
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun doWork(): Result {
        Log.d("NotificationWorker", "Worker started")

        val context = applicationContext
        val remoteDataSource = RemoteDataSourceImpl(RetrofitClient.service)
        val database = AppDatabase.getDatabase(context)
        val notificationDatabase = NotificationDatabase.getDatabase(context)
        val favoritePlaceDao = database.favoritePlaceDao()
        val notificationDao = notificationDatabase.notificationDao()
        val localDataSource = LocalDataSourceImpl(favoritePlaceDao,notificationDao)
        val weatherRepository = WeatherRepositoryImpl(remoteDataSource, localDataSource)

        val locationUtils = LocationUtils(context)

        locationUtils.getLastKnownLocation { lastLocation ->
            if (lastLocation != null) {
                runBlocking {
                    val weatherData = weatherRepository.getCurrentWeather(
                        lastLocation.latitude,
                        lastLocation.longitude,
                        "en",
                        "metric",
                        isOnline = true
                    ).firstOrNull()

                    Log.d("NotificationWorker", "Weather Data: $weatherData")

                    val weatherDescription =
                        weatherData?.weather?.firstOrNull()?.description ?: "Unknown Weather"


                    val time = inputData.getString("notification_time")

                    if (!time.isNullOrEmpty()) {
                        sendNotification(weatherDescription)
                        deleteNotificationFromDatabase(time)
                    }
                }
            } else {
                Log.d("NotificationWorker", "No location found")
            }
        }

        return Result.success()
    }
    private fun deleteNotificationFromDatabase(time: String) {
        val notificationDatabase = NotificationDatabase.getDatabase(applicationContext)
        val notificationDao = notificationDatabase.notificationDao()

        runBlocking {
            notificationDao.deleteNotificationByTime(time)
            Log.d("NotificationWorker", "Notification deleted from database with time: $time")
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun sendNotification(weatherInfo: String) {
        val channelId = "weather_notification_channel"
        val notificationId = 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Weather Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Weather updates notifications"
            }
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🌍 Weather Update")
            .setContentText(weatherInfo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(notificationId, notification)
        scheduleDeletion(notificationId)
    }

    private fun scheduleDeletion(notificationId: Int) {
        val deleteWorkRequest = OneTimeWorkRequestBuilder<DeleteNotificationWorker>()
            .setInitialDelay(1, TimeUnit.HOURS)
            .setInputData(workDataOf("notification_id" to notificationId))
            .build()

        WorkManager.getInstance(applicationContext).enqueue(deleteWorkRequest)
    }
}

class DeleteNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val notificationId = inputData.getInt("notification_id", -1)
        if (notificationId != -1) {
            val notificationDao = NotificationDatabase.getDatabase(applicationContext).notificationDao()
            runBlocking {
                notificationDao.deleteNotificationById(notificationId)
            }
            Log.d("DeleteNotificationWorker", "Deleted notification with ID: $notificationId")
        }
        return Result.success()
    }
}
