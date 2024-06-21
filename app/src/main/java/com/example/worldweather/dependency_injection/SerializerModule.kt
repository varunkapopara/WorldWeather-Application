package com.example.worldweather.dependency_injection

import com.google.gson.Gson
import org.koin.dsl.module

val serializerModule = module {
    single { Gson() }
}