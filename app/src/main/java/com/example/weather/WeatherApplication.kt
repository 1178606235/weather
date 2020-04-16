package com.example.weather

import android.app.Application
import android.content.Context

class WeatherApplication : Application() {
    companion object {
        const val TOKEN = "VB5dNwOGAhYRX6Ra"
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}