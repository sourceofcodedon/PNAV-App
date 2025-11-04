package com.pampang.nav.utilities.di.entrypoint

import com.pampang.nav.utilities.SharedPrefs
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SharedPrefsEntryPoint {
    var sharedPrefs: SharedPrefs
}