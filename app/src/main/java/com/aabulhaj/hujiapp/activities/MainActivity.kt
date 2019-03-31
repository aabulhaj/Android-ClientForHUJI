package com.aabulhaj.hujiapp.activities

import Session
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.EditorInfo
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.callbacks.CaptchaCallback
import com.aabulhaj.hujiapp.callbacks.LoginCallback
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.HttpUrl
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity(), CaptchaCallback, LoginCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Session.isSessionValid()) {
            startActivity(Intent(this, SessionActivity::class.java))
            finish()
            return
        }

        // A call to HUJI site is necessary to fetch the cookies needed for the login request.
        loadSession()

        val id = Session.getId()
        val code = Session.getCode()

        idText.setText(id)
        personalCodeText.setText(code)

        if (id != "" && code != "") {
            captchaText.requestFocus()
        }

        captchaText.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_NEXT) {
                login()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        loginButton.setOnClickListener {
            login()
        }
    }

    private fun loadSession() {
        Session.hujiApiClient.loadLoginPage().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                response?.body()?.close()
                Session.getCaptcha(this@MainActivity)
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                runOnUiThread {
                    showAlertDialog(R.string.login_network_error)
                }
            }
        })
    }

    private fun login() {
        if (!validateFields(true)) {
            return
        }

        enableUIInteraction(false)

        Session.login(this,
                idText.text.toString(),
                personalCodeText.text.toString(),
                captchaText.text.toString())
    }

    override fun onUserAuthenticated(authenticated: Boolean, e: Exception?,
                                     url: HttpUrl?, body: String?) {
        if (!authenticated) {
            runOnUiThread {
                showAlertDialog(e?.message!!)
                enableUIInteraction(true)
            }
            return
        }

        for (segment in url!!.pathSegments()) {
            if (segment.startsWith("!")) {
                Session.setHUJISessionId(segment)
                Session.setId(idText.text.toString())
                Session.setCode(personalCodeText.text.toString())

                startActivity(Intent(this, SessionActivity::class.java))
                finish()
                return
            }
        }

        if (body!!.contains("captcha")) {
            runOnUiThread {
                checkWrongCredentials(body)
            }
        }
    }

    override fun onCaptchaReceived(captcha: Bitmap?, e: Exception?) {
        runOnUiThread {
            if (captcha == null) {
                showAlertDialog(e?.message!!)
                return@runOnUiThread
            }

            captchaImage.setImageBitmap(captcha)
        }
    }

    private fun checkWrongCredentials(body: String) {
        // Load a new captcha.
        Session.getCaptcha(this)

        val start = "<font color=red>"
        val end = "</font>"

        val startIndex = body.indexOf(start) + start.length
        val endIndex = body.indexOf(end)

        val reason = body.substring(startIndex, endIndex)
                .replace(start, "")
                .replace(end, "")
                .trim()

        enableUIInteraction(true)
        captchaText.setText("")

        if (reason == "אין התאמה במילת אימות") {
            captchaText.requestFocus()
            captchaText.error = getString(R.string.wrong_captcha_error)
        } else if (reason == "אין התאמה בין מס הזהות והקוד האישי") {
            personalCodeText.setText("")
            personalCodeText.requestFocus()
            personalCodeText.error = getString(R.string.wrong_username_or_password_error)
        }
    }

    private fun validateFields(showErr: Boolean): Boolean {
        if (idText.text.isEmpty()) {
            if (showErr) {
                idText.requestFocus()
                idText.error = getString(R.string.missing_id_error)
            }
            return false
        } else if (!(idText.text.length == 8 || idText.text.length == 9)) {
            if (showErr) {
                idText.requestFocus()
                idText.error = getString(R.string.incorrect_id_length)
            }
            return false
        } else if (personalCodeText.text.isEmpty()) {
            if (showErr) {
                personalCodeText.requestFocus()
                personalCodeText.error = getString(R.string.missing_personal_code_error)
            }
            return false
        } else if (captchaText.text.isEmpty()) {
            if (showErr) {
                captchaText.requestFocus()
                captchaText.error = getString(R.string.missing_captcha_error)
            }
            return false
        }
        return true
    }

    private fun enableUIInteraction(enabled: Boolean) {
        idText.isEnabled = enabled
        personalCodeText.isEnabled = enabled
        captchaText.isEnabled = enabled
        loginButton.isEnabled = enabled
    }

    private fun showAlertDialog(message: String) {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.error))
                .setMessage(message)
                .setNegativeButton(android.R.string.ok, null)
                .show()
    }

    private fun showAlertDialog(messageId: Int) {
        showAlertDialog(getString(messageId))
    }
}
