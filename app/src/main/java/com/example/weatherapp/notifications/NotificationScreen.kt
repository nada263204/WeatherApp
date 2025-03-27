package com.example.weatherapp.notifications

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationScreen(viewModel: NotificationViewModel = viewModel()) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val notifications by viewModel.notifications.collectAsState(emptyList())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBottomSheet = true },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Notification")
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
            Text("Notifications", fontSize = 24.sp)
            Spacer(modifier = Modifier.height(16.dp))

            if (notifications.isEmpty()) {
                Text("You have no new notifications.")
            } else {
                LazyColumn {
                    items(notifications) { notification ->
                        NotificationCard(notification)
                    }
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            var selectedDate by remember { mutableStateOf("") }
            var selectedTime by remember { mutableStateOf("") }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Select Date & Time", fontSize = 20.sp)

                Spacer(modifier = Modifier.height(16.dp))

                DatePicker(selectedDate) { newDate -> selectedDate = newDate }
                Spacer(modifier = Modifier.height(16.dp))
                TimePicker(selectedTime) { newTime -> selectedTime = newTime }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    if (selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
                        viewModel.addNotification(selectedDate, selectedTime)
                        showBottomSheet = false
                        Toast.makeText(context, "Saved: $selectedDate, $selectedTime", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Please select a date and time!", Toast.LENGTH_LONG).show()
                    }
                }) {
                    Text("Confirm")
                }
            }
        }
    }
}

@Composable
fun DatePicker(selectedDate: String, onDateSelected: (String) -> Unit) {
    val context = LocalContext.current

    Button(onClick = {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                onDateSelected("$year-${month + 1}-$dayOfMonth")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }) {
        Text(if (selectedDate.isEmpty()) "Pick Date" else "Date: $selectedDate")
    }
}

@Composable
fun TimePicker(selectedTime: String, onTimeSelected: (String) -> Unit) {
    val context = LocalContext.current

    Button(onClick = {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hour, minute ->
                onTimeSelected(String.format("%02d:%02d", hour, minute))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }) {
        Text(if (selectedTime.isEmpty()) "Pick Time" else "Time: $selectedTime")
    }
}

@Composable
fun NotificationCard(notification: NotificationEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Date: ${notification.date}", fontSize = 16.sp)
            Text("Time: ${notification.time}", fontSize = 16.sp)
        }
    }
}
