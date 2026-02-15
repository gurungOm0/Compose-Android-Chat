package com.softstudio.chat.models

import com.google.firebase.firestore.ServerTimestamp

data class Message(
    val id: String = "",
    val senderId: String = "",
    val text: String = "",
    val imageUrl: String? = null,
    @ServerTimestamp
    val timestamp: com.google.firebase.Timestamp? = null,
    val readBy: List<String> = emptyList(), // List of UIDs who read it
    val isEdited: Boolean = false
)