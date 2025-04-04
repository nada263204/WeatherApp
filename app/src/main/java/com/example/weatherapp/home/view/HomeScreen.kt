package com.example.weatherapp.home.view

import android.content.Context
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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.example.weatherapp.R
import com.example.weatherapp.data.models.CurrentWeatherState
import com.example.weatherapp.data.models.ForecastWeatherState
import com.example.weatherapp.data.models.ListItem
import com.example.weatherapp.home.viewModel.WeatherViewModel
import com.example.weatherapp.utils.NetworkUtils
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay
import java.text.NumberFormat


@Composable
fun HomeScreen(viewModel: WeatherViewModel, context: Context) {
    val currentWeatherState by viewModel.currentWeatherState.collectAsState()
    val forecastWeatherState by viewModel.forecastWeatherState.collectAsState()
    val location by viewModel.location.collectAsState()

    var isOffline by remember { mutableStateOf(!NetworkUtils.isNetworkAvailable(context)) }
    var alertShown by remember { mutableStateOf(false)}
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val temperatureUnit = remember {
        mutableStateOf(getTemperatureUnitSymbol(context))
    }


    LaunchedEffect(isOffline) {
        if (isOffline&& !alertShown) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "No internet connection,you need connection to get fresh data",
                    duration = SnackbarDuration.Short
                )
            }
        }
        while (true) {
            delay(1000)
            temperatureUnit.value = getTemperatureUnitSymbol(context)
        }
    }

    LaunchedEffect(location, currentWeatherState) {
        location?.let {
            viewModel.fetchCurrentWeather(it.latitude, it.longitude, context)
            viewModel.fetchForecastWeather(it.latitude, it.longitude, context)
        }

        if (currentWeatherState is CurrentWeatherState.Success) {
            val cityName = (currentWeatherState as CurrentWeatherState.Success).data.name
            location?.let {
                viewModel.fetchAndSaveHome(context, it.longitude, it.latitude, cityName)
            }
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        containerColor = Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (currentWeatherState) {
                is CurrentWeatherState.Loading -> {
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
                    val progress by animateLottieCompositionAsState(composition)

                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LottieAnimation(
                            composition = composition,
                            progress = progress,
                            modifier = Modifier.size(100.dp)
                        )
                    }
                }

                is CurrentWeatherState.Success -> {
                    val data = (currentWeatherState as CurrentWeatherState.Success).data
                    Spacer(modifier = Modifier.height(24.dp))

                    CityAndDateSection(data.name)

                    Spacer(modifier = Modifier.height(24.dp))

                    WeatherIcon(
                        conditionId = data.weather[0].icon,
                        description = data.weather[0].description
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    WeatherDetails(
                        temp = (data.main.temp as? Double)?.toInt() ?: 0,
                        wind = (data.wind.speed as? Double)?.toInt() ?: 0,
                        humidity = data.main.humidity as? Int ?: 0,
                        clouds = data.clouds.all as? Int ?: 0,
                        pressure = data.main.pressure as? Int ?: 0,
                        context = LocalContext.current
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(stringResource(R.string.today), fontSize = 20.sp, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    when (forecastWeatherState) {
                        is ForecastWeatherState.Success -> {
                            val forecastData =
                                (forecastWeatherState as ForecastWeatherState.Success).data
                            WeatherForecastSection(forecastData.list,context)
                            Spacer(modifier = Modifier.height(18.dp))
                        }

                        is ForecastWeatherState.Loading ->  {
                            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
                            val progress by animateLottieCompositionAsState(composition)

                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                LottieAnimation(
                                    composition = composition,
                                    progress = progress,
                                    modifier = Modifier.size(100.dp)
                                )
                            }
                        }


                        is ForecastWeatherState.Failure ->  {
                            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.failed))
                            val progress by animateLottieCompositionAsState(composition)

                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                LottieAnimation(
                                    composition = composition,
                                    progress = progress,
                                    modifier = Modifier.size(100.dp)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            stringResource(R.string.this_week),
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    when (forecastWeatherState) {
                        is ForecastWeatherState.Success -> {
                            val forecastData =
                                (forecastWeatherState as ForecastWeatherState.Success).data
                            WeatherDailyForecastSection(forecastData.list,context)
                        }

                        else -> {}
                    }
                }
                is CurrentWeatherState.Failure ->  {
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.failed))
                    val progress by animateLottieCompositionAsState(composition)

                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LottieAnimation(
                            composition = composition,
                            progress = progress,
                            modifier = Modifier.size(100.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherForecastSection(forecastList: List<ListItem>,context: Context) {
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
            val temp = (item.main.temp as? Double)?.toInt() ?: 0
            val temperature = "${formatNumber(temp, context)}${getTemperatureUnitSymbol(context)}"

            WeatherHourCard(
                data = WeatherHourData(formattedTime, temperature, iconRes),
                isSelected = index == selectedIndex,
                onClick = { selectedIndex = if (selectedIndex == index) -1 else index }
            )
        }
    }
}

@Composable
fun WeatherDailyForecastSection(forecastList: List<ListItem>,context: Context) {
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
            val temp = (item.main.temp as? Double)?.toInt() ?: 0
            val temperature = "${formatNumber(temp, context)}${getTemperatureUnitSymbol(context)}"

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
    val currentDate =
        remember { SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date()) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = city, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(text = currentDate, fontSize = 16.sp, color = Color.LightGray)
    }
}

@Composable
fun WeatherDetails(temp: Int, wind: Int, humidity: Int, clouds: Int, pressure: Int, context: Context) {
    val tempUnit by remember { mutableStateOf(getTemperatureUnitSymbol(context)) }
    val windUnit by remember { mutableStateOf(getWindSpeedUnitSymbol(context)) }


    val formattedTemp = formatNumber(temp, context)
    val formattedWind = formatNumber(wind, context)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A).copy(alpha = 0.8f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            WeatherStat(stringResource(R.string.temperature), "$formattedTemp$tempUnit")
            WeatherStat(stringResource(R.string.wind), "$formattedWind $windUnit")
            WeatherStat(stringResource(R.string.humidity), "${formatNumber(humidity,context)}%")
            WeatherStat(stringResource(R.string.clouds), "${formatNumber(clouds,context)}%")
            WeatherStat(stringResource(R.string.pressure), "${formatNumber(pressure,context)} hPa")
        }
    }
}



@Composable
fun WeatherStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 12.sp, color = Color.LightGray)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}


fun getWeatherIcon(conditionId: String): Int {
    return when (conditionId) {
        "01d" -> R.drawable._01d
        "01n" -> R.drawable._01n
        "02d" -> R.drawable._02d
        "02n" -> R.drawable._02d
        "03d" -> R.drawable._03d
        "03n" -> R.drawable._03n
        "04d" -> R.drawable._04d
        "04n" -> R.drawable._04n
        "09d" -> R.drawable._09d
        "09n" -> R.drawable._09n
        "10d" -> R.drawable._10d
        "10n" -> R.drawable._10n
        "11d" -> R.drawable._11d
        "11n" -> R.drawable._11n
        "13d" -> R.drawable._13d
        "13n" -> R.drawable._13n
        "50d" -> R.drawable._50d
        "50n" -> R.drawable._50n
        else -> R.drawable._01n
    }
}

@Composable
fun WeatherHourCard(data: WeatherHourData, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(6.dp)
            .width(60.dp)
            .height(120.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (isSelected) Color(0xFF2A2A2A).copy(alpha = 0.8f)
                else Color(0xFF2A2A2A).copy(alpha = 0.6f)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = data.time,
            fontSize = 12.sp,
            color = Color.LightGray
        )

        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = painterResource(id = data.iconRes),
            contentDescription = "Weather Icon",
            modifier = Modifier.size(36.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = data.temperature,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.LightGray
        )
    }
}



data class WeatherHourData(val time: String, val temperature: String, val iconRes: Int)

@Composable
fun WeatherIcon(conditionId: String, description: String) {
    val iconMap = mapOf(
        "01d" to R.drawable._01d,
        "01n" to R.drawable._01n,
        "02d" to R.drawable._02d,
        "02n" to R.drawable._02n,
        "03d" to R.drawable._03d,
        "03n" to R.drawable._03n,
        "04d" to R.drawable._04d,
        "04n" to R.drawable._04n,
        "09d" to R.drawable._09d,
        "09n" to R.drawable._09n,
        "10d" to R.drawable._10d,
        "10n" to R.drawable._10n,
        "11d" to R.drawable._11d,
        "11n" to R.drawable._11n,
        "13d" to R.drawable._13d,
        "13n" to R.drawable._13n,
        "50d" to R.drawable._50d,
        "50n" to R.drawable._50n
    )



    val iconResId = iconMap[conditionId] ?: R.drawable._01n

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = "Weather Icon",
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}

fun formatNumber(value: Int, context: Context): String {
    val sharedPreferences = context.getSharedPreferences("weather_pref", Context.MODE_PRIVATE)
    val language = sharedPreferences.getString("language", Locale.getDefault().language)

    val locale = if (language == "ar") {
        Locale("ar", "EG")
    } else {
        Locale.getDefault()
    }

    val formatter = NumberFormat.getInstance(locale)
    return formatter.format(value)
}


fun getTemperatureUnitSymbol(context: Context): String {
    val prefs = context.getSharedPreferences("weather_pref", Context.MODE_PRIVATE)
    Log.d("WeatherApp", "Temperature Unit: ${prefs.getString("temperature_unit", "celsius")}")
    return when (prefs.getString("temperature_unit", "celsius")) {
        "Fahrenheit" -> "°F"
        "Kelvin" -> "K"
        else -> "°C"
    }
}

fun getWindSpeedUnitSymbol(context: Context): String {
    val prefs = context.getSharedPreferences("weather_pref", Context.MODE_PRIVATE)
    return when (prefs.getString("wind_speed_unit", "m/s")) {
        "km/h" -> "km/h"
        "mph" -> "mph"
        else -> "m/s"
    }
}








