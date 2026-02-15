package com.softstudio.chat.models.dbmodels

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "conversations",
    indices = [Index(value = ["participantsId"])]
)
data class ConversationDb(
    @PrimaryKey
    val conversationId: String,
    val participantsId: List<String>,
    val participantLookupKey: String,
    val conversationName: String? = null,
    val avatarUrl: String? = null,
    @ColumnInfo(name = "last_message_timestamp")
    val lastMessageTimestamp: Long = 0,
    val unreadCount: Int = 0,
    val isPinned: Boolean = false,
    val lastMessage: String? = null
){
    companion object {
        fun createLookupKey(participants: List<String>): String {
            return participants.sorted().joinToString(separator = "|")
        }
        /*fun createNew(
            participants: List<String>,
            name: String? = null,
            avatar: String? = null
        ): ConversationDb {
            return ConversationDb(
                conversationId = 0, // Default for auto-increment
                participantsId = participants,
                participantLookupKey = createLookupKey(participants),
                conversationName = name,
                avatarUrl = avatar
            )
        }*/
    }
}
