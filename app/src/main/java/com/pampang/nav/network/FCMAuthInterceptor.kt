package com.pampang.nav.network

import android.content.Context
import com.pampang.nav.utils.FCMTokenHelper
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class FCMAuthInterceptor(private val context: Context) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = FCMTokenHelper.getAccessToken(context)
                ?: throw IOException("Failed to get Firebase access token")

        val originalRequest: Request = chain.request()
        val newRequest: Request = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Content-Type", "application/json; UTF-8")
                .build()

        return chain.proceed(newRequest)
    }
}
