package com.example.weatherapp.data.models

import kotlinx.coroutines.flow.Flow

sealed class CurrentWeatherState {
    data object Loading : CurrentWeatherState()
    data class Success(val data: ResponseCurrentWeather) : CurrentWeatherState()
    data class Failure(val error: Throwable) : CurrentWeatherState()
}

sealed class ForecastWeatherState {
    data object Loading : ForecastWeatherState()
    data class Success(val data: Response5days3hours) : ForecastWeatherState()
    data class Failure(val error: Throwable) : ForecastWeatherState()
}
