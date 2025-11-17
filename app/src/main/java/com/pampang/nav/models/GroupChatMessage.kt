package com.pampang.nav.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class GroupChatMessage(
    val senderId: String = "",
    val senderName: String = "",
    val senderRole: String = "",
    val text: String = "",
    @ServerTimestamp
    val timestamp: Date? = null
)