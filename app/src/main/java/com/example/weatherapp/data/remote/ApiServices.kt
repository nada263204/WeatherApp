package com.example.weatherapp.data.remote

import com.example.weatherapp.data.models.Response5days3hours
import com.example.weatherapp.data.models.ResponseCurrentWeather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiServices {
    @GET("data/2.5/weather?")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String = "da5a7b1f5f513a2397980af6ed750940",
        @Query("units") units: String = "metric",
        @Query("lang") lang: String,
    ): Response<ResponseCurrentWeather>


    @GET("data/2.5/forecast?")
    suspend fun get5days3hoursForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") key: String = "da5a7b1f5f513a2397980af6ed750940",
        @Query("units") units: String = "metric",
        @Query("lang") lang: String
    ): Response<Response5days3hours>
}