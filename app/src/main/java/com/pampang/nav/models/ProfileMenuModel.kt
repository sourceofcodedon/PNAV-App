package com.pampang.nav.models

import androidx.annotation.StringRes

data class ProfileMenuModel(
    @StringRes val titleResId: Int,
    val iconResId: Int? = null
)
