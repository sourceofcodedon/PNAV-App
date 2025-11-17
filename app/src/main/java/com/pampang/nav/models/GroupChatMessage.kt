package com.pampang.nav.models

data class GroupChatMessage(
    val senderId: String = "",
    val senderName: String = "",
    val senderRole: String = "",
    val text: String = "",
    val timestamp: Long = 0
)