package com.softstudio.chat.services.repository.implementations

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.softstudio.chat.models.User
import com.softstudio.chat.models.UserProfile
import com.softstudio.chat.models.dbmodels.UserProfileDb
import com.softstudio.chat.services.repository.ProfileService
import com.softstudio.chat.services.safeTrace
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfileServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
) : ProfileService {

    private val userBaseCollection = firestore.collection("users")

    override val currentUserProfile: Flow<UserProfileDb?> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(null)
            close()
            return@callbackFlow
        }
        val subscription = userBaseCollection.document(uid)
            .addSnapshotListener { snapshot, _ ->
                val profile = snapshot?.toObject(UserProfileDb::class.java)
                trySend(profile)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun createOrUpdateProfile(profile: User): Result<Unit> =
        safeTrace("updateProfile") {
            val uid = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            userBaseCollection.document(uid).set(profile, SetOptions.merge()).await()
        }

    override suspend fun uploadProfilePicture(imageUri: Uri): Result<String> =
        safeTrace("uploadProfilePicture") {
            val uid = auth.currentUser?.uid ?: throw Exception("User not authenticated")

            // 1. Create a reference in Storage: /profile_pics/{userId}.jpg
            val storageRef = storage.reference.child("profile_pics/$uid.jpg")

            // 2. Upload the file
            storageRef.putFile(imageUri).await()

            // 3. Get and return the public Download URL
            val downloadUrl = storageRef.downloadUrl.await()
            downloadUrl.toString()
        }

    override suspend fun setUserOnlineStatus(isOnline: Boolean): Result<Unit> =
        safeTrace("setOnlineStatus") {
            val uid = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            userBaseCollection.document(uid).update(
                "isOnline", isOnline,
                "lastSeen", FieldValue.serverTimestamp()
            ).await()
        }

    override suspend fun getProfile(userId: String): Result<UserProfile> =
        safeTrace("getRemoteProfile") {
            val snapshot = userBaseCollection.document(userId).get().await()
            snapshot.toObject(UserProfile::class.java) ?: throw Exception("Profile not found")
        }

    override suspend fun getUser(userId: String): Result<User> =
        safeTrace("getRemoteProfile") {
            val snapshot = userBaseCollection.document(userId).get().await()
            snapshot.toObject(User::class.java) ?: throw Exception("Profile not found")
        }

    override suspend fun searchUsers(query: String): Result<List<UserProfile>> =
        safeTrace("searchUsers") {
            // Note: Firestore performs simple prefix searches
            val snapshot = userBaseCollection
                .whereGreaterThanOrEqualTo("displayName", query)
                .whereLessThanOrEqualTo("displayName", query + "\uf8ff")
                .get().await()

            snapshot.toObjects(UserProfile::class.java)
        }
}