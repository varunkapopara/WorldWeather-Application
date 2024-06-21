package com.example.worldweather.utils

import android.app.Application
import com.example.worldweather.dependency_injection.networkModule
import com.example.worldweather.dependency_injection.repositoryModule
import com.example.worldweather.dependency_injection.serializerModule
import com.example.worldweather.dependency_injection.storageModule
import com.example.worldweather.dependency_injection.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AppConfig : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AppConfig)
            modules(
                listOf(
                    repositoryModule,
                    viewModelModule,
                    serializerModule,
                    storageModule,
                    networkModule
                )
            )
        }
    }
}