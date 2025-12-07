package com.pampang.nav.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.card.MaterialCardView
import com.google.firebase.messaging.FirebaseMessaging
import com.pampang.nav.R

class InboxActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inbox)

        val groupChatCard = findViewById<MaterialCardView>(R.id.groupChatCard)
        groupChatCard.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }

        val announcementCard = findViewById<MaterialCardView>(R.id.announcementCard)
        announcementCard.setOnClickListener {
            val intent = Intent(this, AnnouncementsActivity::class.java)
            startActivity(intent)
        }

        // Subscribe to the announcements topic
        FirebaseMessaging.getInstance().subscribeToTopic("announcements")
            .addOnCompleteListener { task ->
                var msg = "Subscribed to announcements"
                if (!task.isSuccessful) {
                    msg = "Subscription to announcements failed"
                }
                Log.d("InboxActivity", msg)
            }
    }
}
