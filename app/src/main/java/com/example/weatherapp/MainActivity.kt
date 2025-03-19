package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.data.remote.RetrofitClient
import com.example.weatherapp.data.repo.WeatherRepositoryImpl
import com.example.weatherapp.navigations.MainScreen
import com.example.weatherapp.ui.theme.WeatherAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GlobalScope.launch (Dispatchers.IO){
            val test = WeatherRepositoryImpl.getInstance(
                RemoteDataSourceImpl(RetrofitClient.service)
            )
                .getForecastWeather(isOnline = true)
            Log.i("TAG", "data: $test ")
        }

        setContent {
            WeatherAppTheme {
                MainScreen()
            }
        }
    }
}

