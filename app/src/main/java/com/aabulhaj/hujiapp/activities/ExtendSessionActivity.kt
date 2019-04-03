package com.aabulhaj.hujiapp.activities

import Session
import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.ContextThemeWrapper
import android.view.View
import android.view.Window
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.callbacks.CaptchaCallback
import com.aabulhaj.hujiapp.callbacks.LoginCallback
import kotlinx.android.synthetic.main.activity_extend_session.*
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.HttpUrl


class ExtendSessionActivity : Activity(), View.OnClickListener, TextWatcher,
        CaptchaCallback, LoginCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_extend_session)

        captchaAnswer.addTextChangedListener(this)
        captchaAnswer.requestFocus()

        extendButton.isEnabled = false
        extendButton.setOnClickListener(this)

        Session.cleanCookieStore()

        Session.getCaptcha(this, this)
    }

    override fun onClick(view: View?) {
        if (captchaAnswer.text != null) {
            extendButton.isEnabled = false
            Session.login(this, Session.getId(), Session.getCode(),
                    captchaAnswer.text.toString(), this)
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(editable: Editable?) {
        extendButton.isEnabled = !editable.isNullOrEmpty()
    }

    override fun onUserAuthenticated(authenticated: Boolean, e: Exception?, url: HttpUrl?,
                                     body: String?) {
        runOnUiThread {
            if (!authenticated) {
                showAlertDialog(e!!.message!!)
                extendButton.isEnabled = true
            } else {
                for (s in url!!.pathSegments()) {
                    if (s.startsWith("!")) {
                        Session.setHUJISessionId(s)
                        Session.setNeedsExtending(applicationContext, false)
                        finish()
                        return@runOnUiThread
                    }
                }
                if (body!!.contains("captcha")) {
                    extendButton.isEnabled = false
                    showAlertDialog(resources.getString(R.string.wrong_captcha_error))
                    Session.getCaptcha(this, this)
                }
            }
        }
    }

    override fun onCaptchaReceived(captcha: Bitmap?, e: Exception?) {
        runOnUiThread {
            if (captcha != null) {
                captchaIm.setImageBitmap(captcha)
                captchaAnswer.text.clear()
            } else {
                showAlertDialog(e!!.localizedMessage)
            }
        }
    }

    private fun showAlertDialog(message: String) {
        AlertDialog.Builder(ContextThemeWrapper(this, R.style.Dialog))
                .setTitle(getString(R.string.error))
                .setMessage(message)
                .setNegativeButton(android.R.string.ok, null)
                .show()
    }
}
