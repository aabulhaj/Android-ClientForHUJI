package com.aabulhaj.hujiapp

import android.app.Application
import android.content.Context
import android.content.SharedPreferences


class HUJIApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        preferences = getSharedPreferences("HUJI", Context.MODE_PRIVATE)
    }

    companion object {
        lateinit var preferences: SharedPreferences
            private set
    }
}

