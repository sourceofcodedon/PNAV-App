package com.pampang.nav

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.cloudinary.android.MediaManager
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val config = mapOf(
            "cloud_name" to "diy2dfxg1",
            "api_key" to "332781821316882",
            "api_secret" to "-ay1qlKDZgdzVASq5fNaz-mfeAw"
        )
        MediaManager.init(this, config)
    }

}