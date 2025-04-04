package com.example.weatherapp.notifications.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.*
import com.example.weatherapp.R
import com.example.weatherapp.data.local.NotificationEntity
import com.example.weatherapp.notifications.viewModel.NotificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationScreen(viewModel: NotificationViewModel = viewModel()) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()

    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBottomSheet = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_notification)
                )
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.notifications),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (notifications.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.no_notifications),
                    color = Color.LightGray,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(8.dp)
                )
            } else {
                LazyColumn {
                    items(notifications) { notification ->
                        NotificationCard(notification) { id ->
                            viewModel.deleteNotification(id)
                        }
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(id = R.string.select_date_time),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                DatePicker(selectedDate) { newDate -> selectedDate = newDate }
                Spacer(modifier = Modifier.height(10.dp))
                TimePicker(selectedTime) { newTime -> selectedTime = newTime }

                Spacer(modifier = Modifier.height(12.dp))

                Button(onClick = {
                    if (selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
                        viewModel.addNotification(selectedDate, selectedTime)
                        showBottomSheet = false
                        selectedDate = ""
                        selectedTime = ""
                        Toast.makeText(
                            context,
                            context.getString(R.string.saved_notification, selectedDate, selectedTime),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(context, context.getString(R.string.select_date_time_warning), Toast.LENGTH_LONG).show()
                    }
                }) {
                    Text(stringResource(id = R.string.confirm))
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
        Text(if (selectedDate.isEmpty()) stringResource(id = R.string.pick_date) else "${stringResource(id = R.string.date)}: $selectedDate")
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
        Text(if (selectedTime.isEmpty()) stringResource(id = R.string.pick_time) else "${stringResource(id = R.string.time)}: $selectedTime")
    }
}

@Composable
fun NotificationCard(notification: NotificationEntity, onDelete: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(120.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF5B2C6F), Color(0xFF452B8A))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterStart)
            ) {
                Text(
                    text = "${stringResource(id = R.string.date)}: ${notification.date}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${stringResource(id = R.string.time)}: ${notification.time}",
                    fontSize = 16.sp,
                    color = Color.LightGray
                )
            }

            IconButton(
                onClick = { onDelete(notification.time) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(id = R.string.delete),
                    tint = Color.White
                )
            }
        }
    }
}
