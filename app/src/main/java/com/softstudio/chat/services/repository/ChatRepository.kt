package com.softstudio.chat.services.repository

import com.softstudio.chat.localDb.daos.ConversationDao
import com.softstudio.chat.localDb.daos.MessagesDao
import com.softstudio.chat.models.dbmodels.ConversationDb
import com.softstudio.chat.models.dbmodels.MessageDb
import com.softstudio.chat.util.toDbModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val messageService: MessageService,
    private val conversationDao: ConversationDao,
    private val messagesDao: MessagesDao
) {
    // UI Observes this: Live list from Local DB
    fun getConversationsLocal(): Flow<List<ConversationDb>> =
        conversationDao.getConversationList()

    // UI Observes this: Live messages for a specific chat
    fun getMessagesLocal(chatId: String): Flow<List<MessageDb>> =
        conversationDao.getMessagesForConversation(chatId)

    /**
     * Call this in your ViewModel to start syncing Firestore to Room.
     * It listens to the cloud and pushes every update into the local DB.
     */
    fun syncConversations(): Flow<Result<Unit>> = callbackFlow {
        messageService.getActiveConversations().collect { result ->
            result.onSuccess { cloudList ->
                val entities = cloudList.map { it.toDbModel() }
                conversationDao.insertConversations(entities)
                trySend(Result.success(Unit))
            }
            result.onFailure { trySend(Result.failure(it)) }
        }
    }

    suspend fun sendMessage(chatId: String, text: String) =
        messageService.sendMessage(chatId, text, null)
}