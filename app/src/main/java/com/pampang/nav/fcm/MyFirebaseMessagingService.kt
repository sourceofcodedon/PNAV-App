package com.pampang.nav.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pampang.nav.R
import com.pampang.nav.screens.AnnouncementsActivity
import com.pampang.nav.screens.ChatActivity
import com.pampang.nav.screens.buyer.BuyerMainActivity
import com.pampang.nav.screens.seller.SellerMainActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCM"

        fun subscribeToGlobalChatTopic() {
            FirebaseMessaging.getInstance().subscribeToTopic("global_chat")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Subscribed to global_chat topic successfully")
                    } else {
                        Log.e(TAG, "Failed to subscribe to global_chat topic", task.exception)
                    }
                }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val from = remoteMessage.from
        Log.d(TAG, "Message received from: $from")

        // Differentiate between an announcement and a chat message
        if (from?.contains("/topics/announcements") == true) {
            handleAnnouncement(remoteMessage)
        } else {
            handleChatMessage(remoteMessage)
        }
    }

    private fun handleAnnouncement(remoteMessage: RemoteMessage) {
        val notification = remoteMessage.notification
        val title = notification?.title ?: "New Announcement"
        val body = notification?.body ?: "You have a new announcement."

        Log.d(TAG, "Handling announcement: $body")
        
        val intent = Intent(this, AnnouncementsActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        showNotification("announcements_channel", "Announcements", title, body, pendingIntent)
    }

    private fun handleChatMessage(remoteMessage: RemoteMessage) {
        // --- Check if the message is from the current user ---
        val senderId = remoteMessage.data["senderId"]
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        if (senderId != null && senderId == currentUserId) {
            Log.d(TAG, "Ignoring notification from self.")
            return // Don't show notification for your own message
        }
        // ----------------------------------------------------

        val prefs = getSharedPreferences("NotifPrefs", MODE_PRIVATE)
        val isNotifEnabled = prefs.getBoolean("notifications_enabled", true)
        if (!isNotifEnabled) {
            Log.d(TAG, "Notifications are silenced for chat. Skipping display.")
            return
        }

        var title = "PNAV"
        var body = "New message"
        remoteMessage.notification?.let {
            title = it.title ?: title
            body = it.body ?: body
        }

        Log.d(TAG, "Handling chat message: $body")

        if (BuyerMainActivity.isForeground || SellerMainActivity.isForeground) {
            val intent = Intent("NEW_MESSAGE_RECEIVED")
            intent.putExtra("title", title)
            intent.putExtra("body", body)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        } else {
            val intent = Intent(this, ChatActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
            showNotification("pnav_channel", "PNAV Notifications", title, body, pendingIntent)
        }
    }

    private fun showNotification(channelId: String, channelName: String, title: String, message: String, pendingIntent: PendingIntent) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.pnavlogo)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        manager.notify((System.currentTimeMillis()).toInt(), builder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        // If a new token is generated, re-subscribe to topics
        subscribeToGlobalChatTopic()
    }
}
