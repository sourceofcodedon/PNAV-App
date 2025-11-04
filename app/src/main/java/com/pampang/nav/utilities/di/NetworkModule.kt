package com.pampang.nav.utilities.di

import com.google.gson.GsonBuilder
import com.pampang.nav.network.AppService
import dagger.Module
import dagger.Provides
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideInterceptor() = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Singleton
    @Provides
    fun provideClient(loggingInterceptor: HttpLoggingInterceptor) = OkHttpClient.Builder().apply {
        writeTimeout(5, java.util.concurrent.TimeUnit.MINUTES)
        readTimeout(5, java.util.concurrent.TimeUnit.MINUTES)
        connectTimeout(5, java.util.concurrent.TimeUnit.MINUTES)
        addInterceptor(loggingInterceptor)
        addInterceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .build()
            return@addInterceptor chain.proceed(newRequest)
        }
    }.build()

    @AssistedFactory
    interface RetrofitFactory {
        fun create(baseUrl: String): RetrofitBuilder
    }

    class RetrofitBuilder @AssistedInject constructor(
        @Assisted val baseUrl: String,
        val okHttpClient: OkHttpClient
    ) {
        inline fun <reified T> build(): T {
            return Retrofit.Builder().apply {
                baseUrl(baseUrl)
                addConverterFactory(
                    GsonConverterFactory.create(
                        GsonBuilder()
                            .setLenient()
                            .create()
                    )
                )
                client(okHttpClient)
            }.build().create()
        }
    }

    @Provides
    fun provideAppService(retrofitFactory: RetrofitFactory): AppService =
        retrofitFactory.create("https://urlhere/api/").build()
}