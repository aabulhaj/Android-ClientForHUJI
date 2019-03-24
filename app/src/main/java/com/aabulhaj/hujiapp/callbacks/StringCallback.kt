package com.aabulhaj.hujiapp.callbacks

import okhttp3.ResponseBody
import retrofit2.Call

interface StringCallback {
    fun onResponse(call: Call<ResponseBody>?, responseBody: String)
    fun onFailure(call: Call<ResponseBody>?, e: Exception)
}


