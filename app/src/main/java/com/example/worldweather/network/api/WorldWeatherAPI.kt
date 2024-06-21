package com.example.worldweather.network.api

import com.example.worldweather.data.RemoteLocation
import com.example.worldweather.data.RemoteWeatherData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WorldWeatherAPI {
    companion object {
        const val BASE_URL = "https://api.weatherapi.com/v1/"
        const val API_KEY = "07b894a6a5884306ade120508241106"
    }
    @GET("search.json")
    suspend fun searchLocation(
        @Query("key") key: String = API_KEY,
        @Query("q") query: String
    ): Response<List<RemoteLocation>>

    @GET("forecast.json")
    suspend fun getWeatherData(
        @Query("key") key: String = API_KEY,
        @Query("q") query: String
    ): Response<RemoteWeatherData>
}