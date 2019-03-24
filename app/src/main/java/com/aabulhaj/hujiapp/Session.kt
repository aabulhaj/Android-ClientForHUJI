import android.content.res.Resources
import android.graphics.BitmapFactory
import com.aabulhaj.hujiapp.data.HUJI_BASE_URL
import com.aabulhaj.hujiapp.HujiApi
import com.aabulhaj.hujiapp.util.PreferencesUtil
import com.aabulhaj.hujiapp.R
import com.aabulhaj.hujiapp.callbacks.CaptchaCallback
import com.aabulhaj.hujiapp.callbacks.LoginCallback
import com.aabulhaj.hujiapp.interceptors.CookiesInterceptor
import com.aabulhaj.hujiapp.interceptors.HeaderInterceptor
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
    val hujiApiClient: HujiApi
    private val cookieManager: CookieManager = CookieManager()
    private var sessionId: String? = null
    private var id: String? = null

    init {
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)

        loadLastSession()

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
            cookieString += cookie.name + "=" + cookie.value + "; "
        }

        if (!cookieString.isEmpty()) {
            cookieString = cookieString.substring(0, cookieString.length - 2)
        }

        requestBuilder.addHeader("Cookie", cookieString)
        return requestBuilder.build()
    }

    fun login(callback: LoginCallback, unNormalizedId: String, code: String, captchaText: String) {
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
                val errorMessage = Resources.getSystem().getString(R.string.login_network_error)
                callback.onUserAuthenticated(false, Exception(errorMessage), null, null)
            }
        })
    }

    fun getCaptcha(callback: CaptchaCallback) {
        hujiApiClient.getCaptcha().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                val bitmap = BitmapFactory.decodeStream(response?.body()?.byteStream())
                response?.body()?.close()

                if (bitmap != null) {
                    callback.onCaptchaReceived(bitmap, null)
                } else {
                    getCaptcha(callback)
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                val errorMessage = Resources.getSystem().getString(R.string.captcha_network_error)
                callback.onCaptchaReceived(null, Exception(errorMessage))
            }
        })
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

    fun setId(id: String) {
        this.id = id
        PreferencesUtil.putString("id", id)
    }

    fun setCode(code: String) {
        // TODO(aabulhaj): Encrypt the code before storing it.
        PreferencesUtil.putString("code", code)
    }

    fun getCacheKey(value: String): String {
        return id + "_" + value
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