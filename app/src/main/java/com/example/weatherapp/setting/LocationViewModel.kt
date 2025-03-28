package com.example.weatherapp.setting


import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale

class LocationViewModel : ViewModel() {
    private val _selectedPosition = MutableStateFlow<LatLng?>(null)
    val selectedPosition: StateFlow<LatLng?> = _selectedPosition

    private val _placeName = MutableStateFlow("Unknown Place")
    val placeName: StateFlow<String> = _placeName

    fun setSelectedPosition(context: Context, latLng: LatLng) {
        _selectedPosition.value = latLng
        viewModelScope.launch {
            _placeName.value = getPlaceNameFromLatLng(context, latLng.latitude, latLng.longitude)
        }
    }

    private fun getPlaceNameFromLatLng(context: Context, latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addressList = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addressList.isNullOrEmpty()) {
                addressList[0].getAddressLine(0) ?: "Unknown Place"
            } else {
                "Unknown Place"
            }
        } catch (e: IOException) {
            Log.e("Geocoder Error", "Failed to get place name", e)
            "Unknown Place"
        }
    }
}