package com.softstudio.chat.util


import com.softstudio.chat.models.Conversation
import com.softstudio.chat.models.dbmodels.ConversationDb

fun Conversation.toDbModel(): ConversationDb {
    return ConversationDb(
        conversationId = this.conversationId,
        participantsId = this.participantIds,
        // We derive the lookup key from participants for Room query consistency
        participantLookupKey = this.participantIds.sorted().joinToString("|"),
        lastMessageTimestamp = this.lastUpdated?.toDate()?.time ?: 0L,
        conversationName = this.conversationName,
        avatarUrl = this.avatarUrl,
        lastMessage = this.lastMessage,
        unreadCount = 0 // Default for new sync
    )
}
