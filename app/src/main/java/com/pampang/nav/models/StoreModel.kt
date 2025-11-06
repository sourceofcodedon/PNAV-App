package com.pampang.nav.models

import com.google.firebase.firestore.PropertyName

data class StoreModel(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String = "",

    @get:PropertyName("store_name")
    @set:PropertyName("store_name")
    var storeName: String = "",

    @get:PropertyName("opening_time")
    @set:PropertyName("opening_time")
    var openingTime: String = "",

    @get:PropertyName("closing_time")
    @set:PropertyName("closing_time")
    var closingTime: String = "",

    @get:PropertyName("owner_id")
    @set:PropertyName("owner_id")
    var ownerId: String = "",

    @get:PropertyName("image")
    @set:PropertyName("image")
    var image: String? = null
)
