package com.aabulhaj.hujiapp.callbacks

import okhttp3.HttpUrl


interface LoginCallback {
    fun onUserAuthenticated(authenticated: Boolean, e: Exception?, url: HttpUrl?, body: String?)
}