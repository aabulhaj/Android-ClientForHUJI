package com.aabulhaj.hujiapp

import Session
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.AsyncTask
import com.google.gson.GsonBuilder
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.ref.WeakReference
import java.lang.reflect.Type


object Cache {

    fun cacheObject(context: Context?, `object`: Any, token: Type, name: String) {
        if (context == null) {
            return
        }
        try {
            writeString(context, name, GsonBuilder().create().toJson(`object`, token))
        } catch (e: Exception) {
        }

    }

    fun loadCachedObject(context: Context?, token: Type, fileName: String): Any? {
        if (context == null) return null
        return GsonBuilder().create().fromJson(readString(context, fileName), token)
    }

    private fun readString(context: Context, fileName: String): String {
        try {
            val inputStream = context.openFileInput(Session.getCacheKey("$fileName.cache"))
            if (inputStream != null) {

                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                val stringBuilder = StringBuilder()

                var receiveString = bufferedReader.readLine()

                while (receiveString != null) {
                    stringBuilder.append(receiveString)
                    receiveString = bufferedReader.readLine()
                }

                inputStream.close()
                return stringBuilder.toString()
            }
        } catch (e: Exception) {
        }

        return ""
    }

    private fun writeString(context: Context, fileName: String, json: String) {
        MyAsyncTask(context).execute(fileName, json)
    }

    private class MyAsyncTask internal constructor(context: Context) :
            AsyncTask<String, Void, Void>() {
        private val context: WeakReference<Context> = WeakReference(context)

        override fun doInBackground(vararg params: String): Void? {
            try {
                val outputStreamWriter = OutputStreamWriter(context.get()?.openFileOutput(
                        Session.getCacheKey("${params[0]}.cache"), MODE_PRIVATE)
                )
                outputStreamWriter.write(params[1])
                outputStreamWriter.close()
            } catch (e: Exception) {
            }

            return null
        }
    }
}