package com.softstudio.chat.services.repository

import android.net.Uri
import com.softstudio.chat.models.Conversation
import com.softstudio.chat.models.Message
import kotlinx.coroutines.flow.Flow

interface MessageService {
    // Message Streaming
    fun getMessages(chatId: String): Flow<Result<List<Message>>>

    // Core Messaging Actions
    suspend fun sendMessage(chatId: String, text: String, imageUri: Uri? = null): Result<Unit>
    suspend fun uploadImage(imageUri: Uri): Result<String>
    suspend fun deleteMessage(chatId: String, messageId: String): Result<Unit>
    suspend fun editMessage(chatId: String, messageId: String, newText: String): Result<Unit>

    // Chat Metadata & Interactive Features
    fun getTypingStatus(chatId: String): Flow<List<String>> // Returns list of UserIds currently typing
    suspend fun setTypingStatus(chatId: String, isTyping: Boolean): Result<Unit>

    // Engagement
    suspend fun markAsRead(chatId: String, messageId: String): Result<Unit>

    // Conversation Management
    fun getActiveConversations(): Flow<Result<List<Conversation>>>
}
