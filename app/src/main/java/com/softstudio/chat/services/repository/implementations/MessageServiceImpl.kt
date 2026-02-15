package com.softstudio.chat.services.repository.implementations

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.softstudio.chat.models.Conversation
import com.softstudio.chat.models.Message
import com.softstudio.chat.services.repository.MessageService
import com.softstudio.chat.services.safeTrace
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.jvm.java

class MessageServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : MessageService {

    private val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    // 1. Real-time Message Stream
    override fun getMessages(chatId: String): Flow<Result<List<Message>>> = callbackFlow {
        val subscription = firestore.collection(COL_CONVERSATIONS)
            .document(chatId)
            .collection(COL_MESSAGES)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                val messages = snapshot?.toObjects(Message::class.java) ?: emptyList()
                trySend(Result.success(messages))
            }
        awaitClose { subscription.remove() }
    }

    // 2. Send Message (Handles Text and Image URL logic)
    override suspend fun sendMessage(chatId: String, text: String, imageUri: Uri?): Result<Unit> =
        safeTrace("sendMessage") {
            val batch =  firestore.batch()
            val msgRef = firestore.collection(COL_CONVERSATIONS).document(chatId).collection(COL_MESSAGES).document()
            val convRef = firestore.collection(COL_CONVERSATIONS).document(chatId)

            val message = Message(id = msgRef.id, senderId = currentUserId, text = text, timestamp = null)
            batch.set(msgRef,message)

            val convUpdate = mapOf(
                "lastMessage" to text,
                "lastUpdated" to FieldValue.serverTimestamp(),
                "participantIds" to chatId.split("_")
            )
            batch.set(convRef,convUpdate,
                    SetOptions.merge())
            batch.commit().await()
            Unit
        }

    override suspend fun uploadImage(imageUri: Uri): Result<String> = safeTrace("uploadImage") {
        val storageRef = storage.reference.child("chat_images/${com.android.identity.util.UUID.randomUUID()}")

        // Upload the file
        storageRef.putFile(imageUri).await()

        // Get the download URL
        val downloadUrl = storageRef.downloadUrl.await()
        Result.success(downloadUrl).toString()
    }

    override suspend fun deleteMessage(chatId: String, messageId: String): Result<Unit> = safeTrace("deleteMessage"){
        if (currentUserId.isEmpty()) throw Exception("Not authenticated")

        firestore.collection(COL_CONVERSATIONS)
            .document(chatId)
            .collection(COL_MESSAGES)
            .document(messageId)
            .delete()
            .await()
    }

    override suspend fun editMessage(
        chatId: String,
        messageId: String,
        newText: String
    ): Result<Unit> = safeTrace("editMessage"){
        if (currentUserId.isEmpty()) throw Exception("Not authenticated")

        firestore.collection(COL_CONVERSATIONS)
            .document(chatId)
            .collection(COL_MESSAGES)
            .document(messageId)
            .update(
                "text",newText,
                "isEdited",true,
                "editTimestamp", FieldValue.serverTimestamp()
            )
            .await()
    }

    // 3. Typing Status (Real-time list of UIDs typing)
    override fun getTypingStatus(chatId: String): Flow<List<String>> = callbackFlow {
        val subscription = firestore.collection(COL_CONVERSATIONS)
            .document(chatId)
            .collection(COL_TYPING)
            .whereEqualTo("isTyping", true)
            .addSnapshotListener { snapshot, _ ->
                val typingUsers = snapshot?.documents
                    ?.map { it.id }
                    ?.filter { it != currentUserId } ?: emptyList()
                trySend(typingUsers) // Don't show "You are typing"
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun setTypingStatus(chatId: String, isTyping: Boolean): Result<Unit> =
        safeTrace("setTypingStatus") {
            firestore.collection(COL_CONVERSATIONS)
                .document(chatId)
                .collection(COL_TYPING)
                .document(currentUserId)
                .set(mapOf("isTyping" to isTyping))
                .await()
        }

    // 4. Read Receipts
    override suspend fun markAsRead(chatId: String, messageId: String): Result<Unit> =
        safeTrace("markAsRead") {
            firestore.collection(COL_CONVERSATIONS)
                .document(chatId)
                .collection(COL_MESSAGES)
                .document(messageId)
                .update("readBy", FieldValue.arrayUnion(currentUserId))
                .await()
        }

    // 5. Inbox Screen (Stream of active conversations)
    override fun getActiveConversations(): Flow<Result<List<Conversation>>> = callbackFlow {
        val subscription = firestore.collection(COL_CONVERSATIONS)
            .whereArrayContains("participantIds", currentUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                val convos = snapshot?.toObjects(Conversation::class.java) ?: emptyList()
                trySend(Result.success(convos))
            }
        awaitClose { subscription.remove() }
    }

    private companion object {
        const val COL_CONVERSATIONS = "conversations"
        const val COL_MESSAGES = "messages"
        const val COL_TYPING = "typing_indicators"
    }
}