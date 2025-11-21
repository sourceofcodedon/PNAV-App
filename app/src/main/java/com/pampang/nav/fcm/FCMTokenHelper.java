package com.pampang.nav.fcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.auth.oauth2.GoogleCredentials;
import com.pampang.nav.R;

import java.io.InputStream;
import java.util.Collections;

public class FCMTokenHelper {

    private static String cachedToken;
    private static long tokenExpiryTime;

    public static String getAccessToken(Context context) {
        try {
            // Check in-memory cache
            if (cachedToken != null && System.currentTimeMillis() < tokenExpiryTime) {
                return cachedToken;
            }

            // Optional: Check SharedPreferences
            SharedPreferences prefs = context.getSharedPreferences("fcm_token", Context.MODE_PRIVATE);
            String savedToken = prefs.getString("token", null);
            long savedExpiry = prefs.getLong("expiry", 0);
            if (savedToken != null && System.currentTimeMillis() < savedExpiry) {
                cachedToken = savedToken;
                tokenExpiryTime = savedExpiry;
                return cachedToken;
            }

            // Load service account from raw folder
            InputStream serviceAccountStream = context.getResources().openRawResource(R.raw.wag); //sdk file
            GoogleCredentials credentials = GoogleCredentials
                    .fromStream(serviceAccountStream)
                    .createScoped(Collections.singletonList("https://www.googleapis.com/auth/firebase.messaging"));

            credentials.refresh(); // Always fetch once
            String token = credentials.getAccessToken().getTokenValue();
            long expiresAt = credentials.getAccessToken().getExpirationTime().getTime();

            // Cache in memory + persist
            cachedToken = token;
            tokenExpiryTime = expiresAt;

            prefs.edit()
                    .putString("token", cachedToken)
                    .putLong("expiry", tokenExpiryTime)
                    .apply();

            return token;

        } catch (Exception e) {
            Log.e("FCM", "Error generating access token: ", e);
            return null;
        }
    }
}
