package com.softstudio.chat.ui.screens.chat

import com.softstudio.chat.models.dbmodels.MessageDb

data class ChatUiState(
    val conversationName: String? = null,
    val conversationImage: String? = null,

    val currentUserId: String? = null,

    val messages: List<MessageDb>? = null,

    val isLoading: Boolean = false,
    val error: String? = null
)
