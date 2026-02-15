package com.softstudio.chat.services.repository

import android.net.Uri
import com.softstudio.chat.models.User
import com.softstudio.chat.models.UserProfile
import com.softstudio.chat.models.dbmodels.UserProfileDb
import kotlinx.coroutines.flow.Flow


interface ProfileService {
    val currentUserProfile: Flow<UserProfileDb?>

    suspend fun createOrUpdateProfile(profile: User): Result<Unit>
    suspend fun uploadProfilePicture(imageUri: Uri): Result<String> // Returns Download URL

    // Social / Discovery
    suspend fun getProfile(userId: String): Result<UserProfile>
    suspend fun getUser(userId: String): Result<User>
    suspend fun searchUsers(query: String): Result<List<UserProfile>>

    // Status Logic
    suspend fun setUserOnlineStatus(isOnline: Boolean): Result<Unit>
}