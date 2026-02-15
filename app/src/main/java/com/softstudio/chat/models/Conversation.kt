package com.softstudio.chat.models

import com.google.firebase.firestore.ServerTimestamp

data class Conversation(
    val conversationId: String = "",
    val participantIds: List<String> = emptyList(),
    @ServerTimestamp
    val lastUpdated: com.google.firebase.Timestamp? = null,
    val conversationName:String = "",
    val avatarUrl:String = "",
    val lastMessage: String? = null,
    val unreadCount: Int = 0
)