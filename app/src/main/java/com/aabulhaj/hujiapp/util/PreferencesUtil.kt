package com.aabulhaj.hujiapp.util

import com.aabulhaj.hujiapp.HUJIApplication

object PreferencesUtil {
    private val preferences = HUJIApplication.preferences

    fun putString(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, or: String? = null): String? {
        return preferences.getString(key, or)
    }

    fun putInt(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }

    fun getInt(key: String, or: Int = -1): Int {
        return preferences.getInt(key, or)
    }

    fun putBoolean(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, or: Boolean = false): Boolean {
        return preferences.getBoolean(key, or)
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