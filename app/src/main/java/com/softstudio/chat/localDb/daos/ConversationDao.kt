package com.softstudio.chat.localDb.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.softstudio.chat.models.dbmodels.ConversationDb
import com.softstudio.chat.models.dbmodels.MessageDb
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationDb)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(conversations: List<ConversationDb>)

    @Update
    suspend fun updateConversation(conversation: ConversationDb)

    @Query("SELECT * FROM conversations ORDER BY last_message_timestamp DESC")
    fun getConversationList(): Flow<List<ConversationDb>>

    @Query("SELECT * FROM conversations WHERE participantLookupKey = :lookupKey LIMIT 1")
    suspend fun getConversationByLookupKey(lookupKey: String): ConversationDb?

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversation(conversationId: String): Flow<List<MessageDb>>

    @Query("" +
            "UPDATE conversations SET unreadCount = unreadCount + 1," +
            "last_message_timestamp = :timestamp WHERE conversationId = :conversationId" +
            "")
    suspend fun incrementUnreadCount(conversationId: String, timestamp: Long)

    @Query("DELETE FROM conversations WHERE conversationId = :conversationId")
    suspend fun deleteConversation(conversationId: String)

    @Query("UPDATE conversations SET unreadCount = 0 WHERE conversationId = :conversationId")
    suspend fun clearUnreadCount(conversationId: String)
}