package com.pampang.nav.remote;

import android.content.Context;

import com.pampang.nav.fcm.FCMAuthInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit fcmRetrofit = null;
    private static final String FCM_BASE_URL = "https://fcm.googleapis.com/";

    public static Retrofit getFCMClient(Context context) {
        if (fcmRetrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new FCMAuthInterceptor(context))
                    .build();

            fcmRetrofit = new Retrofit.Builder()
                    .baseUrl(FCM_BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return fcmRetrofit;
    }
}
