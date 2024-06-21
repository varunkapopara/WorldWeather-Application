package com.example.worldweather.dependency_injection

import com.example.worldweather.network.repository.WeatherDataRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { WeatherDataRepository(worldWeatherAPI = get()) }
}
