package com.pampang.nav.models

import com.google.firebase.Timestamp

data class NavigationHistoryItem(
    val user_id: String = "",
    val store_name: String = "",
    val timestamp: Timestamp = Timestamp.now()
)
