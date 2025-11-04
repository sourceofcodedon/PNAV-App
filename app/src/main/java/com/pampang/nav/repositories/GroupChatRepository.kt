package com.pampang.nav.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pampang.nav.models.GroupChatMessage
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

    suspend fun sendGroupChatMessage(text: String) {
        val currentUser = auth.currentUser ?: return
        val message = GroupChatMessage(
            senderId = currentUser.uid,
            senderName = currentUser.displayName ?: "Anonymous",
            text = text,
            timestamp = System.currentTimeMillis()
        )
        firestore.collection("global_chat").add(message).await()
    }
}
