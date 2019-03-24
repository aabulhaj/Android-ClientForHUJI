package com.aabulhaj.hujiapp.interceptors

import Session
import okhttp3.Interceptor
import okhttp3.Response

class CookiesInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        proceed(
                Session.prepareRequest(request().newBuilder())
        )
    }
}