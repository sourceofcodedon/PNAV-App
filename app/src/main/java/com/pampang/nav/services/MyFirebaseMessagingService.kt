package com.pampang.nav.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pampang.nav.R
import com.pampang.nav.screens.seller.SellerMainActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val prefs = getSharedPreferences("NotifPrefs", MODE_PRIVATE)
        val isNotifEnabled = prefs.getBoolean("notifications_enabled", true)
        if (!isNotifEnabled) {
            Log.d("FCM", "Notifications are silenced. Skipping display.")
            return
        }

        val title = remoteMessage.notification?.title ?: "Pnav"
        val body = remoteMessage.notification?.body ?: "New message"

        Log.d("FCM", "Message received: $body")

        if (SellerMainActivity.isForeground) {
            val intent = Intent("NEW_MESSAGE_RECEIVED")
            intent.putExtra("title", title)
            intent.putExtra("body", body)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        } else {
            showNotification(title, body)
        }
    }

    private fun showNotification(title: String?, message: String?) {
        val channelId = "pnav_channel"

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Pnav Notifications", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(this, SellerMainActivity::class.java)
        intent.putExtra("openFragment", "MessageFragment")

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.pampang_nav_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

        manager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Refreshed token: $token")
        saveTokenToPrefs(token)
        updateTokenInFirestore(token)
    }

    private fun saveTokenToPrefs(token: String?) {
        val prefs: SharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        prefs.edit().putString("fcmToken", token).apply()
    }

    companion object {
        fun updateTokenInFirestore(token: String?) {
            if (token == null) return
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(user.uid)
                        .update("fcmToken", token)
                        .addOnSuccessListener { Log.d("FCM", "Token updated in Firestore from companion object") }
                        .addOnFailureListener { e -> Log.e("FCM", "Failed to update token from companion object", e) }
            } else {
                Log.w("FCM", "User is null, cannot update token in Firestore from companion object")
            }
        }

        fun getTokenAndUpload() {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }
                val token = task.result
                updateTokenInFirestore(token)
            }
        }
    }
}
