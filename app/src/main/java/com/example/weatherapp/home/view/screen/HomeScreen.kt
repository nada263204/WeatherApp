package com.example.weatherapp.home.view.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R
import com.example.weatherapp.data.models.CurrentWeatherState
import com.example.weatherapp.data.models.ForecastWeatherState
import com.example.weatherapp.data.models.ListItem
import com.example.weatherapp.data.models.Response5days3hours
import com.example.weatherapp.data.models.ResponseCurrentWeather
import com.example.weatherapp.home.viewModel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(viewModel: WeatherViewModel) {
    val currentWeatherState by viewModel.currentWeatherState.collectAsState()
    val forecastWeatherState by viewModel.forecastWeatherState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (currentWeatherState) {
            is CurrentWeatherState.Loading -> Text(
                stringResource(R.string.loading),
                fontSize = 18.sp,
                color = Color.White
            )

            is CurrentWeatherState.Success -> {
                val data = (currentWeatherState as CurrentWeatherState.Success).data
                Spacer(modifier = Modifier.height(40.dp))

                CityAndDateSection(data.name)

                Spacer(modifier = Modifier.height(28.dp))

                WeatherIcon(conditionId = data.weather[0].icon, description = data.weather[0].description)

                Spacer(modifier = Modifier.height(24.dp))

                WeatherDetails(
                    temp = data.main.temp.toString(),
                    wind = data.wind.speed.toString(),
                    humidity = data.main.humidity,
                    clouds = data.clouds.all,
                    pressure = data.main.pressure
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(stringResource(R.string.today), fontSize = 20.sp, color = Color.White)
                }
                Spacer(modifier = Modifier.height(24.dp))

                when (forecastWeatherState) {
                    is ForecastWeatherState.Success -> {
                        val forecastData = (forecastWeatherState as ForecastWeatherState.Success).data
                        WeatherForecastSection(forecastData.list)
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                    is ForecastWeatherState.Loading -> Text(
                        stringResource(R.string.loading_forecast),
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    is ForecastWeatherState.Failure -> Text(
                        stringResource(R.string.failed_to_load_forecast),
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(stringResource(R.string.this_week), fontSize = 20.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(24.dp))

                when (forecastWeatherState) {
                    is ForecastWeatherState.Success -> {
                        val forecastData = (forecastWeatherState as ForecastWeatherState.Success).data
                        WeatherDailyForecastSection(forecastData.list)
                    }
                    else -> {}
                }
            }

            is CurrentWeatherState.Failure -> Text(
                stringResource(R.string.failed_to_load_weather),
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}



@Composable
fun WeatherForecastSection(forecastList: List<ListItem>) {
    var selectedIndex by remember { mutableStateOf(-1) }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        itemsIndexed(forecastList) { index, item ->
            val formattedTime = item.dt_txt.substring(11, 16)
            val iconRes = getWeatherIcon(item.weather[0].icon)
            val temperature = "${item.main.temp}°"

            WeatherHourCard(
                data = WeatherHourData(formattedTime, temperature, iconRes),
                isSelected = index == selectedIndex,
                onClick = { selectedIndex = if (selectedIndex == index) -1 else index }
            )
        }
    }
}

@Composable
fun WeatherDailyForecastSection(forecastList: List<ListItem>) {
    var selectedIndex by remember { mutableStateOf(-1) }

    val dailyForecast = forecastList
        .groupBy { it.dt_txt.substring(0, 10) }
        .map { it.value.first() }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        itemsIndexed(dailyForecast) { index, item ->
            val formattedDate = item.dt_txt.substring(5, 10)
            val iconRes = getWeatherIcon(item.weather[0].icon)
            val temperature = "${item.main.temp}°"

            WeatherHourCard(
                data = WeatherHourData(formattedDate, temperature, iconRes),
                isSelected = index == selectedIndex,
                onClick = { selectedIndex = if (selectedIndex == index) -1 else index }
            )
        }
    }
}



@Composable
fun CityAndDateSection(city: String) {
    val currentDate = remember { SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date()) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = city, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(text = currentDate, fontSize = 16.sp, color = Color.LightGray)
    }
}

@Composable
fun WeatherDetails(temp: Any, wind: Any, humidity: Int, clouds: Int, pressure: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        WeatherStat(stringResource(R.string.temperature), "${temp}°C")
        WeatherStat(stringResource(R.string.wind), "${wind} km/h")
        WeatherStat(stringResource(R.string.humidity), "$humidity%")
        WeatherStat(stringResource(R.string.clouds), "$clouds%")
        WeatherStat(stringResource(R.string.pressure), "$pressure hPa")
    }
}

@Composable
fun WeatherStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 14.sp, color = Color.LightGray)
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

fun getWeatherIcon(conditionId: String): Int {
    return when (conditionId) {
        "01d", "01n" -> R.drawable._01n
        "02n" -> R.drawable._02n
        "03d" -> R.drawable._03d
        "03n" -> R.drawable._03n
        "04d" -> R.drawable._04d
        "04n" -> R.drawable._04n
        "13d" -> R.drawable._13d
        "13n" -> R.drawable._13n
        else -> R.drawable._01n
    }
}

@Composable
fun WeatherHourCard(data: WeatherHourData, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(6.dp)
            .width(160.dp)
            .height(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFF6A0DAD) else Color(0xFF2A2A2A))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = data.iconRes),
            contentDescription = "Weather Icon",
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(text = data.time, fontSize = 14.sp, color = Color.LightGray)
            Text(text = data.temperature, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

data class WeatherHourData(val time: String, val temperature: String, val iconRes: Int)
@Composable
fun WeatherIcon(conditionId: String, description: String) {
    val iconMap = mapOf(
        "01d" to R.drawable._01n,
        "01n" to R.drawable._01n,
        "02n" to R.drawable._02n,
        "03d" to R.drawable._03d,
        "03n" to R.drawable._03n,
        "04d" to R.drawable._04d,
        "04n" to R.drawable._04n,
        "13d" to R.drawable._13d,
        "13n" to R.drawable._13n,
    )

    val iconResId = iconMap[conditionId] ?: R.drawable._01n

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = "Weather Icon",
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = description, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
    }
}


