package com.example.weatherapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.weatherapp.data.local.AppDatabase
import com.example.weatherapp.data.local.LocalDataSourceImpl
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.data.remote.RetrofitClient
import com.example.weatherapp.data.repo.LocationRepository
import com.example.weatherapp.data.repo.WeatherRepositoryImpl
import com.example.weatherapp.navigations.MainScreen
import com.example.weatherapp.notifications.NotificationDatabase
import com.example.weatherapp.setting.LanguageChangeHelper
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.utiles.LocationUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var weatherRepository: WeatherRepositoryImpl
    private lateinit var locationRepository: LocationRepository



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LanguageChangeHelper.getSavedLanguage(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val database = AppDatabase.getDatabase(this)
        val notificationDatabase = NotificationDatabase.getDatabase(this)
        val localDataSource = LocalDataSourceImpl(database.favoritePlaceDao(),notificationDatabase.notificationDao())

        val remoteDataSource = RemoteDataSourceImpl(services = RetrofitClient.service)
        weatherRepository = WeatherRepositoryImpl.getInstance(remoteDataSource,localDataSource)
        locationRepository = LocationRepository(LocationUtils(this))

        requestLocationPermission { lat, lon ->
            setContent {
                WeatherAppTheme {
                    MainScreen(
                        latitude = lat,
                        longitude = lon,
                        weatherRepository = weatherRepository,
                        locationRepository = locationRepository
                    )
                }
            }
        }
    }

    private fun requestLocationPermission(onLocationReceived: (Double, Double) -> Unit) {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (fineLocationGranted || coarseLocationGranted) {
                getLastKnownLocation(onLocationReceived)
            } else {
                Log.e("MainActivity", "⚠️ Location permission denied! Using default location.")
                onLocationReceived(DEFAULT_LAT, DEFAULT_LON)
            }
        }

        if (!hasLocationPermissions()) {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            getLastKnownLocation(onLocationReceived)
        }
    }

    private fun getLastKnownLocation(onLocationReceived: (Double, Double) -> Unit) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.e("MainActivity", "⚠️ Location permission not granted. Using default location.")
            onLocationReceived(DEFAULT_LAT, DEFAULT_LON)
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    Log.d("MainActivity", "Location retrieved: ${location.latitude}, ${location.longitude}")
                    onLocationReceived(location.latitude, location.longitude)
                } else {
                    Log.e("MainActivity", "Location is null. Using default coordinates.")
                    onLocationReceived(DEFAULT_LAT, DEFAULT_LON)
                }
            }
            .addOnFailureListener { e ->
                Log.e("MainActivity", "Failed to get location: ${e.message}")
                onLocationReceived(DEFAULT_LAT, DEFAULT_LON)
            }
    }

    private fun hasLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val DEFAULT_LAT = 37.7749
        private const val DEFAULT_LON = -122.4194
    }
}
