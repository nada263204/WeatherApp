package com.example.weatherapp.setting

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
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
import com.example.weatherapp.R
import java.util.Locale

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(LocalContext.current))) {
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    val selectedTemperatureUnit by viewModel.selectedTemperatureUnit.collectAsState()
    val selectedWindSpeedUnit by viewModel.selectedWindSpeedUnit.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

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
                            onClick = { viewModel.updateLocation(option) }
                        )
                        .padding(8.dp)
                ) {
                    RadioButton(
                        selected = (selectedLocation == option),
                        onClick = { viewModel.updateLocation(option) }
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
            options = listOf("m/s", "mph"),
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
fun DropdownMenuSetting(options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(selectedOption, color = Color.White)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option, color = Color.White) }, onClick = {
                    onOptionSelected(option)
                    expanded = false
                })
            }
        }
    }
}
