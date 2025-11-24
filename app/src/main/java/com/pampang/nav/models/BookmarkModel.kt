package com.pampang.nav.models

import com.google.firebase.firestore.PropertyName

data class BookmarkModel(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String = "",

    @get:PropertyName("store_id")
    @set:PropertyName("store_id")
    var storeId: String = "",

    @get:PropertyName("user_id")
    @set:PropertyName("user_id")
    var userId: String = ""
)
