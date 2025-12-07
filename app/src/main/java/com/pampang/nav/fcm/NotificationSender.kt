package com.pampang.nav.fcm

import android.content.Context
import android.util.Log
import com.google.gson.JsonObject
import com.pampang.nav.network.FCMApi
import com.pampang.nav.remote.RetrofitClient
import com.pampang.nav.utils.FCMTokenHelper
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread

object NotificationSender {

    private const val PROJECT_ID = "pampangnav"

    @JvmStatic
    fun sendNotificationToGroup(context: Context, title: String, body: String, senderId: String) {
        thread {
            try {
                val accessToken = FCMTokenHelper.getAccessToken(context)
                if (accessToken == null) {
                    Log.e("FCM", "No access token generated")
                    return@thread
                }

                // Build payload
                val payload = JsonObject().apply {
                    val message = JsonObject().apply {
                        val notification = JsonObject().apply {
                            addProperty("title", title)
                            addProperty("body", body)
                        }
                        val data = JsonObject().apply {
                            addProperty("senderId", senderId)
                        }
                        addProperty("topic", "global_chat")
                        add("notification", notification)
                        add("data", data)
                    }
                    add("message", message)
                }

                // Send request
                val api = RetrofitClient.getFCMClient(context).create(FCMApi::class.java)
                val url = "v1/projects/$PROJECT_ID/messages:send"

                api.sendMessage(url, payload).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            Log.d("FCM", "✅ Notification sent successfully to group")
                        } else {
                            try {
                                Log.e("FCM", "❌ Failed: ${response.code()} | ${response.errorBody()?.string()}")
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("FCM", "💥 Error sending notification to group", t)
                    }
                })

            } catch (e: Exception) {
                Log.e("FCM", "Error in notification sender thread", e)
            }
        }
    }
}
