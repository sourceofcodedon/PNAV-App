package com.pampang.nav.utils

import android.content.Context
import android.util.Log
import com.google.gson.JsonObject
import com.pampang.nav.network.FCMApi
import com.pampang.nav.network.RetrofitClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun sendNotification(context: Context, recipientToken: String, title: String, body: String) {
    val projectId = "pampangnav"
    Log.d("FCM_SEND", "Attempting to send notification to token: $recipientToken")

    Thread {
        try {
            val accessToken = FCMTokenHelper.getAccessToken(context)
            if (accessToken == null) {
                Log.e("FCM_SEND", "No access token generated")
                return@Thread
            }
            Log.d("FCM_SEND", "Access token generated successfully")

            // Build payload
            val payload = JsonObject()
            val message = JsonObject()
            val notification = JsonObject()

            notification.addProperty("title", title)
            notification.addProperty("body", body)

            message.addProperty("token", recipientToken)
            message.add("notification", notification)
            payload.add("message", message)

            Log.d("FCM_SEND", "Payload: ${payload.toString()}")

            // Send request (Retrofit handles its own threading)
            val api: FCMApi = RetrofitClient.getFCMClient(context).create(FCMApi::class.java)
            val url = "v1/projects/$projectId/messages:send"

            api.sendMessage(url, payload).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Log.d("FCM_SEND", "✅ Notification sent successfully to token: $recipientToken. Response: ${response.body()?.string()}")
                    } else {
                        try {
                            val errorBody = response.errorBody()?.string()
                            Log.e("FCM_SEND", "❌ Failed to send to token: $recipientToken. Code: ${response.code()} | Body: $errorBody")
                        } catch (e: Exception) {
                            Log.e("FCM_SEND", "Error parsing error body for token: $recipientToken", e)
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("FCM_SEND", "💥 Error sending notification to token: $recipientToken", t)
                }
            })

        } catch (e: Exception) {
            Log.e("FCM_SEND", "Error generating access token for token: $recipientToken", e)
        }
    }.start()
}
