package com.pampang.nav.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pampang.nav.R;
import com.pampang.nav.screens.ChatActivity;
import com.pampang.nav.screens.buyer.BuyerMainActivity;
import com.pampang.nav.screens.seller.SellerMainActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // --- Check if the message is from the current user ---
        String senderId = remoteMessage.getData().get("senderId");
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (senderId != null && senderId.equals(currentUserId)) {
            Log.d(TAG, "Ignoring notification from self.");
            return; // Don't show notification for your own message
        }
        // ----------------------------------------------------

        SharedPreferences prefs = getSharedPreferences("NotifPrefs", MODE_PRIVATE);
        boolean isNotifEnabled = prefs.getBoolean("notifications_enabled", true);
        if (!isNotifEnabled) {
            Log.d(TAG, "Notifications are silenced. Skipping display.");
            return;
        }

        String title = "PNAV";
        String body = "New message";
        if (remoteMessage.getNotification() != null) {
            if (remoteMessage.getNotification().getTitle() != null) {
                title = remoteMessage.getNotification().getTitle();
            }
            if (remoteMessage.getNotification().getBody() != null) {
                body = remoteMessage.getNotification().getBody();
            }
        }

        Log.d(TAG, "Message received: " + body);

        if (BuyerMainActivity.isForeground || SellerMainActivity.isForeground) {
            Intent intent = new Intent("NEW_MESSAGE_RECEIVED");
            intent.putExtra("title", title);
            intent.putExtra("body", body);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } else {
            showNotification(title, body);
        }
    }

    private void showNotification(String title, String message) {
        String channelId = "pnav_channel";

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, "PNAV Notifications", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, ChatActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.pnavlogo)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);
        // If a new token is generated, re-subscribe to the topic
        subscribeToGlobalChatTopic();
    }

    public static void subscribeToGlobalChatTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("global_chat")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Subscribed to global_chat topic successfully");
                    } else {
                        Log.e(TAG, "Failed to subscribe to global_chat topic", task.getException());
                    }
                });
    }
}
