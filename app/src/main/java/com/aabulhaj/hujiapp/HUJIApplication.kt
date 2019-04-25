package com.aabulhaj.hujiapp

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatDelegate
import com.aabulhaj.hujiapp.util.PreferencesUtil


class HUJIApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        preferences = getSharedPreferences("HUJI", Context.MODE_PRIVATE)

        val nightMode = PreferencesUtil.getInt("dark_theme", AppCompatDelegate.MODE_NIGHT_NO)
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    companion object {
        lateinit var preferences: SharedPreferences
            private set
    }
}

