package com.pampang.nav.repositories

import android.content.Context
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pampang.nav.fcm.NotificationSender
import com.pampang.nav.models.GroupChatMessage
import com.pampang.nav.utilities.SharedPrefs
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
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

                val messages = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val senderId = doc.getString("senderId") ?: ""
                        val senderName = doc.getString("senderName") ?: ""
                        val senderRole = doc.getString("senderRole") ?: "user"
                        val text = doc.getString("text") ?: ""

                        val timestampObject = doc.get("timestamp")
                        val date = when (timestampObject) {
                            is Timestamp -> timestampObject.toDate()
                            is Long -> Date(timestampObject)
                            else -> null
                        }

                        GroupChatMessage(senderId, senderName, senderRole, text, date)
                    } catch (e: Exception) {
                        Log.e("GROUP_CHAT_REPO", "Error parsing message", e)
                        null
                    }
                } ?: emptyList()

                trySend(messages)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun sendGroupChatMessage(text: String, context: Context) {
        val currentUser = auth.currentUser ?: return

        val userRole = try {
            val userDoc = firestore.collection("users").document(currentUser.uid).get().await()
            userDoc.getString("role") ?: "role_not_found"
        } catch (e: Exception) {
            Log.e("GROUP_CHAT_REPO", "Error fetching user role", e)
            "fetch_failed"
        }

        val senderName = currentUser.displayName ?: "Anonymous"

        val message = GroupChatMessage(
            senderId = currentUser.uid,
            senderName = senderName,
            senderRole = userRole,
            text = text
        )

        try {
            firestore.collection("global_chat").add(message).await()

            // --- SEND NOTIFICATION ---
            val notificationTitle = "New Message"
            val notificationBody = "$senderName: $text"
            // Include the sender's ID
            NotificationSender.sendNotificationToGroup(context, notificationTitle, notificationBody, currentUser.uid)
            // -------------------------

        } catch (e: Exception) {
            Log.e("GROUP_CHAT_REPO", "Error saving message", e)
        }
    }

    suspend fun getLastReadTimestamp(): Date? {
        val currentUser = auth.currentUser ?: return null
        return try {
            val userDoc = firestore.collection("users").document(currentUser.uid).get().await()
            userDoc.getDate("lastReadTimestamp")
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateLastReadTimestamp(timestamp: Date) {
        val currentUser = auth.currentUser ?: return
        try {
            firestore.collection("users").document(currentUser.uid)
                .update("lastReadTimestamp", timestamp).await()
        } catch (e: Exception) {
            Log.e("GROUP_CHAT_REPO", "Error updating last read timestamp", e)
        }
    }
}
