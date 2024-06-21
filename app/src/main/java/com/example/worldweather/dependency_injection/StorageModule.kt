package com.example.worldweather.dependency_injection

import com.example.worldweather.Storage.SharedPreferencesManager
import org.koin.dsl.module

val storageModule = module {
    single { SharedPreferencesManager(context = get (), gson = get()) }
}