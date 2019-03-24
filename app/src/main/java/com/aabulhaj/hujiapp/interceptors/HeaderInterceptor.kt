package com.aabulhaj.hujiapp.interceptors

import com.aabulhaj.hujiapp.data.HUJI_LOGIN_URL
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        proceed(
                request()
                        .newBuilder()
                        .addHeader("Referer", HUJI_LOGIN_URL)
                        .addHeader("Connection", "keep-alive")
                        .build()
        )
    }
}