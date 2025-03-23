package com.example.weatherapp.data.repo

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherapp.Utiles.LocationUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationRepository(private val locationUtils: LocationUtils) {

    val locationLiveData: LiveData<Location?> = locationUtils.locationLiveData

    //Geocoder
}
