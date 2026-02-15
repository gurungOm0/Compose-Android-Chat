package com.softstudio.chat.services.repository

import android.content.Context
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.auth.FirebaseUser
import com.softstudio.chat.models.User
import kotlinx.coroutines.flow.Flow

interface AccountService {
    val currentUserId: String
    val hasUser: Boolean

    val currentUser: Flow<User>

    val googleIdOption: GetGoogleIdOption
    val request: GetCredentialRequest

    suspend fun signUp(email: String, password: String): Result<Unit>
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun updateProfileImage(url: String): Result<Unit>
    suspend fun getUser(): Result<FirebaseUser?>
    suspend fun signInWithGoogle(context: Context): Result<Unit>
    suspend fun sendRecoveryEmail(email: String): Result<Unit>
    suspend fun createAnonymousAccount(): Result<String>
    suspend fun linkAccount(email: String, password: String): Result<Unit>
    suspend fun linkAccountWithGoogle(context: Context): Result<Unit>
    suspend fun deleteAccount(): Result<Unit>
    suspend fun signOut(): Result<Unit>
}