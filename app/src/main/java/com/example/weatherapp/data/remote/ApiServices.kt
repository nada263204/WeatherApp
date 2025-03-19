package com.example.weatherapp.data.remote

import com.example.weatherapp.data.models.Response5days3hours
import com.example.weatherapp.data.models.ResponseCurrentWeather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiServices {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(@Query("lat") lat:String = "30.6118656",
                                  @Query("lon") lon:String = "32.2895872",
                                  @Query("appid") appid:String = "451666318959ca261cd48d55ee0dcf30",
                                  @Query("units") units:String = "metric",
                                  @Query("lang") lang:String = ""
    ): Response<ResponseCurrentWeather>


    @GET("data/2.5/forecast")
    suspend  fun get5days3hoursForecast(@Query("lat") lat:String = "30.6118656",
                                        @Query("lon") lon:String = "32.2895872",
                                        @Query("appid") key:String = "451666318959ca261cd48d55ee0dcf30",
                                        @Query("units") units:String = "metric",
                                        @Query("lang") lang:String = ""): Response<Response5days3hours>
}