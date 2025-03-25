package com.example.weatherapp.data.remote

import android.util.Log
import com.example.weatherapp.data.models.Response5days3hours
import com.example.weatherapp.data.models.ResponseCurrentWeather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class RemoteDataSourceImpl(private val services: ApiServices) : RemoteDataSource {

    override fun getCurrentWeather(lat: Double, lon: Double ,lang : String,units: String) = flow {
        services.getCurrentWeather(lat,lon,lang=lang, units = units).body()?.let {
            emit(it)
        }
    }

    override fun getForecastWeather(lat: Double, lon: Double ,lang : String,units: String) = flow {
        services.get5days3hoursForecast(lat,lon, lang = lang, units = units).body()?.let {
            emit(it)
        }
    }
}
