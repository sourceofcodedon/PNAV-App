package com.pampang.nav.models

data class ProfileMenuModel(
    val title: String,
    val iconResId: Int? = null
)

val profileMenus = listOf(
    ProfileMenuModel(
        title = "Personal Detail",
    ),
    ProfileMenuModel(
        title = "Contact Us",
    ),
    ProfileMenuModel(
        title = "Privacy and Security",
    ),
    ProfileMenuModel(
        title = "Preferences",
    ),
    ProfileMenuModel(
        title = "Logout",
    ),
)
