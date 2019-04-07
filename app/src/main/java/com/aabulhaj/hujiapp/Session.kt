import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v4.app.FragmentActivity
import android.support.v4.content.LocalBroadcastManager
import android.widget.Toast
import com.aabulhaj.hujiapp.HUJIApplication
import com.aabulhaj.hujiapp.HujiApi
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.callbacks.CaptchaCallback
import com.aabulhaj.hujiapp.callbacks.LoginCallback
import com.aabulhaj.hujiapp.callbacks.StringCallback
import com.aabulhaj.hujiapp.data.HUJI_BASE_URL
import com.aabulhaj.hujiapp.interceptors.CookiesInterceptor
import com.aabulhaj.hujiapp.interceptors.HeaderInterceptor
import com.aabulhaj.hujiapp.util.PreferencesUtil
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.HttpCookie
import java.net.URI
import java.util.concurrent.TimeUnit


object Session {
    const val INTENT_APP_LOGGED_OUT = "com.akramabulhaj.hujiapp.ACTION.APP_LOGGED_OUT"
    const val ACTION_SESSION_STATE_CHANGED = "com.aabulhaj.hujiapp.ACTION_SESSION_STATE_CHANGED"
    const val EXTRA_NEEDS_EXTENDING = "needs_extending"

    var sessionExpired = false
    lateinit var hujiApiClient: HujiApi

    private lateinit var cookieManager: CookieManager
    private var sessionId: String? = null
    private var id: String? = null
    private var code: String? = null

    init {
        initClient()
        loadLastSession()
    }

    fun initClient() {
        cookieManager = CookieManager()
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)

        val okhttpClient = OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(HeaderInterceptor())
                .addInterceptor(CookiesInterceptor())
                .cookieJar(JavaNetCookieJar(cookieManager))
                .build()

        hujiApiClient = Retrofit.Builder()
                .baseUrl(HUJI_BASE_URL)
                .client(okhttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(HujiApi::class.java)
    }

    fun prepareRequest(requestBuilder: Request.Builder): Request {
        var cookieString = ""
        for (cookie in cookieManager.cookieStore.cookies) {
            if (cookie.name.startsWith("\$Path")) {
                continue
            }
            cookieString += cookie.name + "=" + cookie.value + "; "
        }

        if (!cookieString.isEmpty()) {
            cookieString = cookieString.substring(0, cookieString.length - 2)
        }

        requestBuilder.addHeader("Cookie", cookieString)
        return requestBuilder.build()
    }

    fun getSessionUrl(url: String): String {
        return String.format("/dataj/controller/$sessionId/$url")
    }

    fun getHujiSessionUrl(path: String): String {
        return "https://www.huji.ac.il/dataj/controller/$sessionId/$path"
    }

    fun callRequest(request: () -> Call<ResponseBody>, activity: FragmentActivity,
                    callback: StringCallback) {
        request().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if (response == null) {
                    return
                }
                val body = response.body()?.string()!!
                response.body()?.close()

                if (body.contains("captcha")) {
                    sessionExpired = true
                    setNeedsExtending(activity, sessionExpired)
                    callback.onFailure(call, Exception(activity.getString(R.string.extend_session)))
                } else if (body.contains("Internal server did not return a value :")) {
                    val errorMsg = activity.getString(R.string.website_down_for_maintenance)
                    activity.runOnUiThread {
                        Toast.makeText(activity, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                    callback.onFailure(call, Exception(errorMsg))
                } else {
                    callback.onResponse(call, body)
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                val errorMsg = activity.getString(R.string.login_network_error)
                activity.runOnUiThread {
                    Toast.makeText(activity, errorMsg, Toast.LENGTH_SHORT).show()
                }
                callback.onFailure(call, Exception(errorMsg))
            }
        })
    }

    fun login(callback: LoginCallback, unNormalizedId: String, code: String,
              captchaText: String, context: Context) {
        val id = unNormalizedId.substring(0, 8)

        hujiApiClient.login(id, code, captchaText).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if (response?.body() == null) {
                    return
                }

                val url = response.raw().request().url()
                val body = response.body()?.string()
                response.body()?.close()

                callback.onUserAuthenticated(true, null, url, body)
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                val errorMessage = context.resources.getString(R.string.login_network_error)
                callback.onUserAuthenticated(false, Exception(errorMessage), null, null)
            }
        })
    }

    fun getCaptcha(callback: CaptchaCallback, context: Context) {
        hujiApiClient.getCaptcha().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                val bitmap = BitmapFactory.decodeStream(response?.body()?.byteStream())
                response?.body()?.close()

                if (bitmap != null) {
                    callback.onCaptchaReceived(bitmap, null)
                } else {
                    getCaptcha(callback, context)
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                // TODO(aabulhaj): Separate Android code from Kotlin code.
                val errorMessage = context.resources.getString(R.string.captcha_network_error)
                callback.onCaptchaReceived(null, Exception(errorMessage))
            }
        })
    }

    fun logout(context: Context) {
        initClient()
        destroySavedSession()
        LocalBroadcastManager.getInstance(context).sendBroadcast(Intent(INTENT_APP_LOGGED_OUT))
    }

    fun destroySavedSession() {
        val keys = PreferencesUtil.getStringSet("cached_cookie_keys") ?: return
        val editor = HUJIApplication.preferences.edit()

        for (key in keys) {
            editor.remove(key)
        }

        editor.remove("cached_cookie_keys")
        editor.remove("huji_session_id")
        editor.apply()
    }

    fun isSessionValid(): Boolean {
        return PreferencesUtil.contains("cached_cookie_keys")
    }

    fun setHUJISessionId(sessionId: String) {
        this.sessionId = sessionId

        val keys = HashSet<String>()
        for (cookie in cookieManager.cookieStore.cookies) {
            PreferencesUtil.putString(cookie.name, cookie.value)
            keys.add(cookie.name)
        }
        PreferencesUtil.putStringSet("cached_cookie_keys", keys)
        PreferencesUtil.putString("huji_session_id", sessionId)
    }

    fun getId(): String {
        if (id == null) {
            id = PreferencesUtil.getString("id", "")
        }
        return id!!
    }

    fun setId(id: String) {
        this.id = id
        PreferencesUtil.putString("id", id)
    }

    fun getCode(): String {
        if (code == null) {
            code = PreferencesUtil.getString("code", "")
        }
        return code!!
    }

    fun setCode(code: String) {
        // TODO(aabulhaj): Encrypt the code before storing it.
        this.code = code
        PreferencesUtil.putString("code", code)
    }

    fun getCacheKey(value: String): String {
        return id + "_" + value
    }

    fun setNeedsExtending(activity: Context, sessionExpired: Boolean) {
        this.sessionExpired = sessionExpired
        LocalBroadcastManager.getInstance(activity)
                .sendBroadcast(Intent(ACTION_SESSION_STATE_CHANGED)
                        .putExtra(EXTRA_NEEDS_EXTENDING, sessionExpired))
    }

    private fun loadLastSession() {
        val keys = PreferencesUtil.getStringSet("cached_cookie_keys") ?: return
        val HUJIURI = URI.create("https://www.huji.ac.il/dataj/controller/stu/?")

        for (key in keys) {
            val value = PreferencesUtil.getString(key)
            val cookie = HttpCookie(key, value)
            if ("HSC" == key) {
                cookie.path = "/"
            } else {
                cookie.path = "/dataj/"
            }
            cookieManager.cookieStore.add(HUJIURI, cookie)
        }

        sessionId = PreferencesUtil.getString("huji_session_id")
        id = PreferencesUtil.getString("id")
    }
}