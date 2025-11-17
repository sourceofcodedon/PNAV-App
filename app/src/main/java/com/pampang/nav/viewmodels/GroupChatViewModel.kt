package com.pampang.nav.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pampang.nav.repositories.GroupChatRepository
import com.pampang.nav.utilities.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupChatViewModel @Inject constructor(
    private val groupChatRepository: GroupChatRepository,
    private val application: Application
) : ViewModel() {

    val messages = groupChatRepository.getGroupChatMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _networkError = MutableSharedFlow<Unit>()
    val networkError = _networkError.asSharedFlow()

    fun sendMessage(text: String) {
        if (NetworkUtils.isNetworkAvailable(application)) {
            viewModelScope.launch {
                groupChatRepository.sendGroupChatMessage(text, application)
            }
        } else {
            viewModelScope.launch {
                _networkError.emit(Unit)
            }
        }
    }
}
