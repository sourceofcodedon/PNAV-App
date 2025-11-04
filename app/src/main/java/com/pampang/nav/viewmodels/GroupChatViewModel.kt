package com.pampang.nav.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pampang.nav.repositories.GroupChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupChatViewModel @Inject constructor(
    private val groupChatRepository: GroupChatRepository
) : ViewModel() {

    val messages = groupChatRepository.getGroupChatMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun sendMessage(text: String) {
        viewModelScope.launch {
            groupChatRepository.sendGroupChatMessage(text)
        }
    }
}
