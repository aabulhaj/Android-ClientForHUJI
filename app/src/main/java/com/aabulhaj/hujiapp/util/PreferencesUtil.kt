package com.aabulhaj.hujiapp.util

import com.aabulhaj.hujiapp.HUJIApplication

object PreferencesUtil {
    private val preferences = HUJIApplication.preferences

    fun putString(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    fun getString(key: String): String? {
        return preferences.getString(key, null)
    }

    fun putStringSet(key: String, value: HashSet<String>) {
        preferences.edit().putStringSet(key, value).apply()
    }

    fun getStringSet(key: String): Set<String>? {
        return preferences.getStringSet(key, null)
    }

    fun contains(key: String): Boolean {
        return HUJIApplication.preferences.contains(key)
    }
}