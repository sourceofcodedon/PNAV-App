package com.pampang.nav.utils

import android.content.Context
import android.util.Log
import com.google.auth.oauth2.GoogleCredentials
import com.pampang.nav.R
import java.io.InputStream
import java.util.Collections

object FCMTokenHelper {

    private var cachedToken: String? = null
    private var tokenExpiryTime: Long = 0

    @JvmStatic
    fun getAccessToken(context: Context): String? {
        try {
            // Check in-memory cache
            if (cachedToken != null && System.currentTimeMillis() < tokenExpiryTime) {
                return cachedToken
            }

            // Optional: Check SharedPreferences
            val prefs = context.getSharedPreferences("fcm_token", Context.MODE_PRIVATE)
            val savedToken = prefs.getString("token", null)
            val savedExpiry = prefs.getLong("expiry", 0)
            if (savedToken != null && System.currentTimeMillis() < savedExpiry) {
                cachedToken = savedToken
                tokenExpiryTime = savedExpiry
                return cachedToken
            }

            // Load service account from raw folder
            val serviceAccountStream: InputStream = context.resources.openRawResource(R.raw.wag) //sdk file
            val credentials = GoogleCredentials
                    .fromStream(serviceAccountStream)
                    .createScoped(Collections.singletonList("https://www.googleapis.com/auth/firebase.messaging"))

            credentials.refresh() // Always fetch once
            val token = credentials.accessToken.tokenValue
            val expiresAt = credentials.accessToken.expirationTime.time

            // Cache in memory + persist
            cachedToken = token
            tokenExpiryTime = expiresAt

            prefs.edit()
                    .putString("token", cachedToken)
                    .putLong("expiry", tokenExpiryTime)
                    .apply()

            return token

        } catch (e: Exception) {
            Log.e("FCM", "Error generating access token: ", e)
            return null
        }
    }
}
