package com.pampang.nav.repositories

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pampang.nav.models.GroupChatMessage
import com.pampang.nav.utils.sendNotification
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GroupChatRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
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
        Log.d("GROUP_CHAT_REPO", "sendGroupChatMessage called by user: ${currentUser.uid}")

        val message = GroupChatMessage(
            senderId = currentUser.uid,
            senderName = currentUser.displayName ?: "Anonymous",
            text = text,
            timestamp = System.currentTimeMillis()
        )

        try {
            firestore.collection("global_chat").add(message).await()
            Log.d("GROUP_CHAT_REPO", "Message successfully saved to Firestore.")
        } catch (e: Exception) {
            Log.e("GROUP_CHAT_REPO", "Error saving message to Firestore", e)
            return // Don't proceed if message saving fails
        }

        // Send notifications to all users except the sender
        Log.d("GROUP_CHAT_REPO", "Starting to send notifications...")
        try {
            val usersSnapshot = firestore.collection("users").get().await()
            Log.d("GROUP_CHAT_REPO", "Successfully fetched ${usersSnapshot.size()} users.")
            val senderName = currentUser.displayName ?: "Anonymous"
            val notificationTitle = "New Message from $senderName"
            val notificationBody = text

            for (document in usersSnapshot.documents) {
                val userId = document.id
                Log.d("GROUP_CHAT_REPO", "Processing user: $userId")

                if (userId == currentUser.uid) {
                    Log.d("GROUP_CHAT_REPO", "Skipping notification for self (user: $userId)")
                    continue
                }

                val token = document.getString("fcmToken")
                if (token != null && token.isNotEmpty()) {
                    Log.d("GROUP_CHAT_REPO", "Found token for user $userId. Preparing to send notification.")
                    sendNotification(context, token, notificationTitle, notificationBody)
                } else {
                    Log.w("GROUP_CHAT_REPO", "No FCM token found for user: $userId")
                }
            }
            Log.d("GROUP_CHAT_REPO", "Finished processing all users for notifications.")
        } catch (e: Exception) {
            Log.e("GROUP_CHAT_REPO", "Error fetching users or sending notifications", e)
        }
    }
}
