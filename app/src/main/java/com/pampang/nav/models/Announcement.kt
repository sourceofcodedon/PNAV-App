package com.pampang.nav.models

import com.google.firebase.firestore.DocumentId

data class Announcement(
    @DocumentId val id: String = "",
    val title: String = "",
    val message: String = "",
    val timestamp: Long = 0
)