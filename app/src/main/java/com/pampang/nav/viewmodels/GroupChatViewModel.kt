package com.pampang.nav.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pampang.nav.models.GroupChatMessage
import com.pampang.nav.repositories.GroupChatRepository
import com.pampang.nav.utilities.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
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

    private val _replyToMessage = MutableStateFlow<GroupChatMessage?>(null)
    val replyToMessage = _replyToMessage.asStateFlow()

    fun setReplyToMessage(message: GroupChatMessage?) {
        _replyToMessage.value = message
    }

    fun sendMessage(text: String) {
        if (NetworkUtils.isNetworkAvailable(application)) {
            viewModelScope.launch {
                val replyTo = _replyToMessage.value
                if (replyTo != null) {
                    groupChatRepository.sendGroupChatMessage(
                        text,
                        application,
                        replyTo.senderId,
                        replyTo.senderName,
                        replyTo.text
                    )
                    _replyToMessage.value = null // Reset after sending
                } else {
                    groupChatRepository.sendGroupChatMessage(text, application)
                }
            }
        } else {
            viewModelScope.launch {
                _networkError.emit(Unit)
            }
        }
    }

    fun updateLastReadTimestamp(timestamp: Date) {
        viewModelScope.launch {
            groupChatRepository.updateLastReadTimestamp(timestamp)
        }
    }

    suspend fun getLastReadTimestamp(): Date? {
        return groupChatRepository.getLastReadTimestamp()
    }
}
