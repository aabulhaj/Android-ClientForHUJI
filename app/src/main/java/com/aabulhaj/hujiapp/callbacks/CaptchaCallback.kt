package com.aabulhaj.hujiapp.callbacks

import android.graphics.Bitmap

interface CaptchaCallback {
    fun onCaptchaReceived(captcha: Bitmap?, e: Exception?)
}