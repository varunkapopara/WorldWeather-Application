package com.example.worldweather.network.repository

import android.annotation.SuppressLint
import android.location.Geocoder
import com.example.worldweather.data.CurrentLocation
import com.example.worldweather.data.RemoteLocation
import com.example.worldweather.data.RemoteWeatherData
import com.example.worldweather.network.api.WorldWeatherAPI
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.lang.StringBuilder

class WeatherDataRepository (private val worldWeatherAPI: WorldWeatherAPI){

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        fusedLocationProviderClient: FusedLocationProviderClient,
        onSuccess: (currentLocation: CurrentLocation) -> Unit,
        onFailure: () -> Unit
    ) {
        fusedLocationProviderClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener { location ->
         location ?: onFailure()
                onSuccess(
                    CurrentLocation(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                )

        }.addOnFailureListener { onFailure() }
    }

    @Suppress("DEPRECATION")
    fun updateAddressText(
        currentLocation: CurrentLocation,
        geocoder: Geocoder
    ): CurrentLocation {
        val latitude = currentLocation.latitude ?: return currentLocation
        val longitude = currentLocation.longitude ?: return currentLocation
        return geocoder.getFromLocation(latitude, longitude, 1)?.let { addresses ->
           val address = addresses[0]
           val addressText =  StringBuilder()
           addressText.append(address.locality).append(", ")
            addressText.append(address.adminArea).append(", ")
            addressText.append(address.countryName)
            currentLocation.copy(
                location = addressText.toString()
            )
        } ?: currentLocation
    }
    suspend fun searchLocation(query: String): List<RemoteLocation>? {
        val response = worldWeatherAPI.searchLocation(query = query)
        return if(response.isSuccessful) response.body() else null
    }
    suspend fun getWeatherData(latitude: Double, longitude: Double): RemoteWeatherData? {
       val response = worldWeatherAPI.getWeatherData(query = "$latitude,$longitude")
        return if (response.isSuccessful) response.body() else null
    }

}


