package com.softstudio.chat.ui.screens.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softstudio.chat.services.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    val repository: ChatRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val chatId: String = checkNotNull(savedStateHandle["chatId"])
    val messages = repository.getMessagesLocal(chatId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),emptyList())

    fun sendMessages(text: String){
        viewModelScope.launch {
            repository.sendMessage(chatId,text)
        }
    }
}