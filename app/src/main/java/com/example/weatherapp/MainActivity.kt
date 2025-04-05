package com.example.weatherapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.weatherapp.data.local.AppDatabase
import com.example.weatherapp.data.local.LocalDataSourceImpl
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.data.remote.RetrofitClient
import com.example.weatherapp.data.repo.LocationRepository
import com.example.weatherapp.data.repo.WeatherRepositoryImpl
import com.example.weatherapp.navigations.MainScreen
import com.example.weatherapp.data.local.NotificationDatabase
import com.example.weatherapp.utiles.LanguageChangeHelper
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.utiles.LocationUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var weatherRepository: WeatherRepositoryImpl
    private lateinit var locationRepository: LocationRepository

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LanguageChangeHelper.getSavedLanguage(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val database = AppDatabase.getDatabase(this)
        val notificationDatabase = NotificationDatabase.getDatabase(this)
        val homeDatabase = database.homeScreenDao()
        val localDataSource = LocalDataSourceImpl(database.favoritePlaceDao(), notificationDatabase.notificationDao(), homeDatabase)

        val remoteDataSource = RemoteDataSourceImpl(services = RetrofitClient.service)
        weatherRepository = WeatherRepositoryImpl.getInstance(remoteDataSource, localDataSource, this)
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

            onLocationReceived(DEFAULT_LAT, DEFAULT_LON)
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    onLocationReceived(location.latitude, location.longitude)
                } else {
                    onLocationReceived(DEFAULT_LAT, DEFAULT_LON)
                }
            }
            .addOnFailureListener { e ->
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
