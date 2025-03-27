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
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.weatherapp.data.local.AppDatabase
import com.example.weatherapp.data.local.LocalDataSource
import com.example.weatherapp.data.local.LocalDataSourceImpl
import com.example.weatherapp.data.models.CurrentWeatherState
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.data.remote.RetrofitClient
import com.example.weatherapp.data.repo.LocationRepository
import com.example.weatherapp.data.repo.WeatherRepositoryImpl
import com.example.weatherapp.home.viewModel.WeatherViewModel
import com.example.weatherapp.utiles.LocationUtils
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

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
        val favoritePlaceDao = database.favoritePlaceDao()
        val localDataSource = LocalDataSourceImpl(favoritePlaceDao)
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
                    sendNotification(weatherDescription)
                }
            } else {
                Log.d("NotificationWorker", "No location found")
            }
        }

        return Result.success()
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
    }
}