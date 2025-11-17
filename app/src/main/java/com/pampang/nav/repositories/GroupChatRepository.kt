package com.pampang.nav.repositories

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pampang.nav.constants.SharedPrefsConst
import com.pampang.nav.models.GroupChatMessage
import com.pampang.nav.utils.sendNotification
import com.pampang.nav.utilities.SharedPrefs
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GroupChatRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val sharedPrefs: SharedPrefs
) {

    fun getGroupChatMessages(): Flow<List<GroupChatMessage>> = callbackFlow {
        val subscription = firestore.collection("global_chat")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val messages = snapshot?.toObjects(GroupChatMessage::class.java) ?: emptyList()
                trySend(messages)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun sendGroupChatMessage(text: String, context: Context) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("GROUP_CHAT_REPO", "Cannot send message, user is not authenticated.")
            return
        }

        var userRole: String
        try {
            val userDoc = firestore.collection("users").document(currentUser.uid).get().await()
            userRole = userDoc.getString("role") ?: "role_not_found"
        } catch (e: Exception) {
            Log.e("GROUP_CHAT_REPO", "Error fetching user role", e)
            userRole = "fetch_failed"
        }

        val message = GroupChatMessage(
            senderId = currentUser.uid,
            senderName = currentUser.displayName ?: "Anonymous",
            senderRole = userRole,
            text = text,
            timestamp = System.currentTimeMillis()
        )

        try {
            firestore.collection("global_chat").add(message).await()
        } catch (e: Exception) {
            Log.e("GROUP_CHAT_REPO", "Error saving message to Firestore", e)
            return
        }

        // Notification logic remains the same
        try {
            val usersSnapshot = firestore.collection("users").get().await()
            val senderName = currentUser.displayName ?: "Anonymous"
            val notificationTitle = "New Message from $senderName"
            val notificationBody = text

            for (document in usersSnapshot.documents) {
                val userId = document.id
                if (userId == currentUser.uid) continue

                val token = document.getString("fcmToken")
                if (token != null && token.isNotEmpty()) {
                    sendNotification(context, token, notificationTitle, notificationBody)
                } else {
                    Log.w("GROUP_CHAT_REPO", "No FCM token for user: $userId")
                }
            }
        } catch (e: Exception) {
            Log.e("GROUP_CHAT_REPO", "Error sending notifications", e)
        }
    }
}
