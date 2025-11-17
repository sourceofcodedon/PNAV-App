package com.pampang.nav.network

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val FCM_BASE_URL = "https://fcm.googleapis.com/"

    private var fcmRetrofit: Retrofit? = null

    fun getFCMClient(context: Context): Retrofit {
        if (fcmRetrofit == null) {
            val client = OkHttpClient.Builder()
                    .addInterceptor(FCMAuthInterceptor(context))
                    .build()

            fcmRetrofit = Retrofit.Builder()
                    .baseUrl(FCM_BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }
        return fcmRetrofit!!
    }
}
