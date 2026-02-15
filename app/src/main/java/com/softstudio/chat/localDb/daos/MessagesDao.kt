package com.softstudio.chat.localDb.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.softstudio.chat.models.dbmodels.MessageDb

@Dao
interface MessagesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(message: MessageDb): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageDb>)

    @Update
    suspend fun updateMessage(message: MessageDb): Int

    @Query("DELETE FROM messages WHERE  messageId = :messageId")
    suspend fun deleteMessage(messageId: String): Int

}