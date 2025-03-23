package com.example.weatherapp.Utiles

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LocationUtils (context: Context){
    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    val locationLiveData = MutableLiveData<Location?>()

     init {
         fetchLocation()
    }


    @SuppressLint("MissingPermission")
     fun fetchLocation() {
        val locationRequest = LocationRequest.Builder(0)
            .apply { setPriority(Priority.PRIORITY_HIGH_ACCURACY) }.build()
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationLiveData.value = locationResult.lastLocation
                //Log.i("Locationnn", "onLocationResult: ${locationLiveData.value}")
            }
        }
        fusedLocationClient.requestLocationUpdates (locationRequest, locationCallback, Looper.getMainLooper())
    }

    //fun getLocation(): LiveData<Location?> = locationLiveData
}