package com.aabulhaj.hujiapp.callbacks

import okhttp3.ResponseBody
import retrofit2.Call

interface NotebookCallback {
    fun onResponse(call: Call<ResponseBody>?, responseBody: ResponseBody)
    fun onFailure(call: Call<ResponseBody>?, e: Exception)
}

