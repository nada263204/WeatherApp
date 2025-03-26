package com.example.weatherapp.setting

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.weatherapp.home.viewModel.WeatherViewModel
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import java.io.IOException
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MapScreen(
    navController: NavController,
    weatherViewModel: WeatherViewModel,
    locationViewModel: LocationViewModel = remember { LocationViewModel() }
) {
    val initialPosition = LatLng(30.0444, 31.2357)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 12f)
    }

    val context = LocalContext.current
    val selectedPosition by locationViewModel.selectedPosition.collectAsState()
    val placeName by locationViewModel.placeName.collectAsState()

    val backgroundColor = MaterialTheme.colorScheme.background
    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Location", color = onPrimaryColor) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = onPrimaryColor)
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = primaryColor
                )
            )
        },
        containerColor = backgroundColor
    ) {
        Column(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
            GoogleMap(
                modifier = Modifier.weight(1f),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    Log.d("MapClick", "Clicked at: ${latLng.latitude}, ${latLng.longitude}")
                    locationViewModel.setSelectedPosition(context, latLng)
                }
            ) {
                selectedPosition?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = placeName,
                    )
                }
            }

            selectedPosition?.let { position ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(surfaceColor, shape = RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = placeName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = onSurfaceColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = {
                                    selectedPosition?.let { position ->
                                        weatherViewModel.fetchAndSaveFavoritePlace(
                                            context,
                                            position.latitude,
                                            position.longitude,
                                            placeName
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FavoriteBorder,
                                    contentDescription = "Add to Favorites",
                                    tint = primaryColor
                                )
                            }

                            Button(
                                onClick = {
                                    weatherViewModel.setUserSelectedLocation(position.latitude, position.longitude)
                                    navController.popBackStack()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                            ) {
                                Text("Apply", color = onPrimaryColor)
                            }
                        }
                    }
                }
            }
        }
    }
}


fun getPlaceNameFromLatLng(context: Context, latitude: Double, longitude: Double): String {
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
