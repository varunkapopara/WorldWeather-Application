package com.example.worldweather.dependency_injection

import com.example.worldweather.fragments.home.HomeViewModel
import com.example.worldweather.fragments.location.LocationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel{ HomeViewModel(weatherDataRepository = get()) }
    viewModel{ LocationViewModel(weatherDataRepository = get ()) }
}