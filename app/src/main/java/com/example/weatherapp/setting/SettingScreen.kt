package com.example.weatherapp.setting

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.weatherapp.R
import com.example.weatherapp.utiles.LocationUtils
import com.example.weatherapp.data.repo.LocationRepository
import com.example.weatherapp.home.viewModel.WeatherViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(LocalContext.current, LocationRepository(locationUtils = LocationUtils(LocalContext.current)))),
    weatherViewModel: WeatherViewModel
) {
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    val selectedTemperatureUnit by viewModel.selectedTemperatureUnit.collectAsState()
    val selectedWindSpeedUnit by viewModel.selectedWindSpeedUnit.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.settings), fontSize = 24.sp, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))

        Text(stringResource(R.string.select_location), fontSize = 18.sp, color = Color.White)
        Row(Modifier.selectableGroup()) {
            listOf("GPS", "Map").forEach { option ->
                Row(
                    Modifier
                        .selectable(
                            selected = (selectedLocation == option),
                            onClick = {
                                viewModel.updateLocation(option)
                                if (option == "Map") {
                                    navController.navigate("map")
                                } else if (option == "GPS") {
                                    currentLocation?.let { location ->
                                        weatherViewModel.setUserSelectedLocation(location.latitude, location.longitude,context)
                                    }
                                }
                            }
                        )
                        .padding(8.dp)
                ) {
                    RadioButton(
                        selected = (selectedLocation == option),
                        onClick = null
                    )
                    Text(option, Modifier.padding(start = 8.dp), color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(stringResource(R.string.temperature_unit), fontSize = 18.sp, color = Color.White)
        DropdownMenuSetting(
            options = listOf("Kelvin", "Celsius", "Fahrenheit"),
            selectedOption = selectedTemperatureUnit,
            onOptionSelected = { viewModel.updateTemperatureUnit(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(stringResource(R.string.wind_speed_unit), fontSize = 18.sp, color = Color.White)
        DropdownMenuSetting(
            options = if (selectedTemperatureUnit == "Kelvin") listOf("mph") else listOf("m/s", "mph"),
            selectedOption = selectedWindSpeedUnit,
            onOptionSelected = { viewModel.updateWindSpeedUnit(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(stringResource(R.string.language), fontSize = 18.sp, color = Color.White)
        DropdownMenuSetting(
            options = listOf("English", "Arabic"),
            selectedOption = if (selectedLanguage == "ar") "Arabic" else "English",
            onOptionSelected = { viewModel.updateLanguage(it) }
        )
    }
}

@Composable
fun DropdownMenuSetting(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        ) {
            Text(selectedOption)
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
