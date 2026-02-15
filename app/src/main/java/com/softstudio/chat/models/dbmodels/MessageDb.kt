package com.softstudio.chat.models.dbmodels

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.softstudio.chat.models.MessageStatus
import com.softstudio.chat.models.MessageType

@Entity(
    tableName = "messages",
    indices = [
        Index(value = ["conversationId"]),
        Index(value = ["timestamp"]),
        Index(value = ["senderId"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = ConversationDb::class,
            parentColumns = ["conversationId"],
            childColumns = ["conversationId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MessageDb(
    @PrimaryKey(autoGenerate = false)
    val messageId: String,
    val conversationId: String,
    val senderId: String = "",
    val receiverId: String = "",
    val text: String = "",
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val messageType: MessageType?,
    val messageStatus: MessageStatus?,
)