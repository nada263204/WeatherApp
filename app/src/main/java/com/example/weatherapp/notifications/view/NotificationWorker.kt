package com.example.weatherapp.notifications.view

import android.Manifest
import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.weatherapp.utiles.PreferenceManager
import com.example.weatherapp.data.local.AppDatabase
import com.example.weatherapp.data.local.LocalDataSourceImpl
import com.example.weatherapp.data.local.NotificationDatabase
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.data.remote.RetrofitClient
import com.example.weatherapp.data.repo.WeatherRepositoryImpl
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

import com.example.weatherapp.utils.NetworkUtils

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun doWork(): Result {

        val context = applicationContext
        val remoteDataSource = RemoteDataSourceImpl(RetrofitClient.service)
        val database = AppDatabase.getDatabase(context)
        val notificationDatabase = NotificationDatabase.getDatabase(context)
        val favoritePlaceDao = database.favoritePlaceDao()
        val notificationDao = notificationDatabase.notificationDao()
        val homeDao = database.homeScreenDao()
        val localDataSource = LocalDataSourceImpl(favoritePlaceDao, notificationDao, homeDao)
        val weatherRepository = WeatherRepositoryImpl(remoteDataSource, localDataSource, context)

        val preferenceManager = PreferenceManager(context)
        val location = preferenceManager.getLocation()

        if (NetworkUtils.isNetworkAvailable(context)) {
            if (location != null) {
                val (lat, lon) = location
                runBlocking {
                    val weatherData = weatherRepository.getCurrentWeather(
                        lat, lon, "en", "metric"
                    ).firstOrNull()


                    val weatherDescription = weatherData?.weather?.firstOrNull()?.description ?: "Unknown Weather"
                    val time = inputData.getString("notification_time")

                    if (!time.isNullOrEmpty()) {
                        sendNotification(weatherDescription)
                        deleteNotificationByTime(time)
                    }
                }
            }
        } else {
            sendNotification("No network to get data")
        }

        return Result.success()
    }

    private fun deleteNotificationByTime(time: String) {
        val notificationDatabase = NotificationDatabase.getDatabase(applicationContext)
        val notificationDao = notificationDatabase.notificationDao()

        runBlocking {
            notificationDao.deleteNotificationByTime(time)
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
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentTitle("Weather Update 🌍")
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
        }
        return Result.success()
    }
}
