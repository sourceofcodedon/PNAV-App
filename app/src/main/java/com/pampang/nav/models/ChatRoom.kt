package com.pampang.nav.models

import com.google.firebase.firestore.DocumentId

data class ChatRoom(
    @DocumentId
    val id: String = "",
    val participants: List<String> = emptyList(),
    val participantInfo: Map<String, ParticipantInfo> = emptyMap(),
    val lastMessage: String = "",
    val lastMessageTimestamp: Long = 0
)

data class ParticipantInfo(
    val name: String = ""
)