package com.example.weatherapp.data.repo

import android.location.Location
import androidx.lifecycle.LiveData
import com.example.weatherapp.utiles.LocationUtils

class LocationRepository(private val locationUtils: LocationUtils) {

    val locationLiveData: LiveData<Location?> = locationUtils.locationLiveData

    fun getLastKnownLocation(): Location? {
        return locationLiveData.value
    }

    //geocoder
}
