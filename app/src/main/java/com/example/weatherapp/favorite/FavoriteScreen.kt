package com.example.weatherapp.favorite

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.weatherapp.data.local.FavoritePlace
import com.example.weatherapp.home.viewModel.WeatherViewModel

@Composable
fun FavoriteScreen(
    navController: NavController,
    favoriteViewModel: FavoriteViewModel,
    weatherViewModel: WeatherViewModel
) {
    var searchText by remember { mutableStateOf("") }
    val favoritePlaces by favoriteViewModel.favoritePlaces.collectAsState()

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("map") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Place, contentDescription = "Open Map")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Search for Weather", fontSize = 24.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(16.dp))

            BasicTextField(
                value = searchText,
                onValueChange = { searchText = it },
                textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(favoritePlaces) { place ->
                    FavoritePlaceItem(
                        place = place,
                        onDelete = { favoriteViewModel.removeFavoritePlace(it) },
                        onPlaceSelected = { lat, lon ->
                            weatherViewModel.setUserSelectedLocation(lat, lon)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoritePlaceItem(
    place: FavoritePlace,
    onDelete: (FavoritePlace) -> Unit,
    onPlaceSelected: (Double, Double) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
                onPlaceSelected(place.coord.lat, place.coord.lon)
            },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = place.cityName, fontSize = 18.sp, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onBackground)
            IconButton(onClick = { onDelete(place) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
