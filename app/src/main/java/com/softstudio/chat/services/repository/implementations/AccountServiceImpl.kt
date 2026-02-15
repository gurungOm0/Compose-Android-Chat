package com.softstudio.chat.services.repository.implementations

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialCustomException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.softstudio.chat.models.User
import com.softstudio.chat.services.repository.AccountService
import com.softstudio.chat.services.safeTrace
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.security.SecureRandom
import java.util.Base64
import javax.inject.Inject
import androidx.core.net.toUri

class AccountServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val firebaseUser: FirebaseUser
) : AccountService {
    override val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    override val hasUser: Boolean
        get() = auth.currentUser != null

    override val currentUser: Flow<User>
        get() = callbackFlow {
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    this.trySend(auth.currentUser?.let {
                        User(
                            id = it.uid,
                            displayName = it.displayName,
                            isAnonymous = it.isAnonymous
                        )
                    } ?: User())
                }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }

    override val googleIdOption: GetGoogleIdOption
        get() = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(WEB_CLIENT_ID)
            .setNonce(nonce = null)
            .build()

    override val request: GetCredentialRequest
        get() = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

    override suspend fun getUser(): Result<FirebaseUser?> {
        return safeTrace("getUser"){
            auth.currentUser
        }
    }

    override suspend fun signUp(email: String, password: String): Result<Unit> {
        return safeTrace("signUpWithEmailAndPassword"){
            auth.createUserWithEmailAndPassword(email,password).await()
        }
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        return safeTrace("signInWithEmailAndPassword") {
            auth.signInWithEmailAndPassword(email, password).await()
        }
    }

    override suspend fun updateProfileImage(url: String): Result<Unit> = try {
        val user = auth.currentUser
        if (user != null) {
            val profileUpdates = userProfileChangeRequest {
                photoUri = url.toUri()
            }
            user.updateProfile(profileUpdates).await()
            Result.success(Unit)
        } else {
            Result.failure(Exception("No user logged in"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override suspend fun signInWithGoogle(context: Context): Result<Unit> {
        return safeTrace("signInWithGoogle") {
            var e = signInG(request, context)
            // In this case, we attempt to sign in again with filtering disabled.
            if (e is NoCredentialException) {
                val googleIdOptionFalse: GetGoogleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(WEB_CLIENT_ID)
                    .setNonce(generateSecureRandomNonce())
                    .build()

                val requestFalse: GetCredentialRequest = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOptionFalse)
                    .build()
                e = signInG(requestFalse, context)
            }
            if (e != null) {
                throw e
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateSecureRandomNonce(byteLength: Int = 32): String {
        val randomBytes = ByteArray(byteLength)
        SecureRandom().nextBytes(randomBytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes)
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    suspend fun signInG(request: GetCredentialRequest, context: Context): Exception? {
        val credentialManager = CredentialManager.create(context)
        val failureMessage = "Sign in failed!"
        delay(250)
        return try {
            val result = credentialManager.getCredential(
                request = request,
                context = context,
            )
            Log.i(TAG, result.toString())
            Log.i(TAG, "(☞ﾟヮﾟ)☞  Sign in Successful!  ☜(ﾟヮﾟ☜)")
            null

        } catch (e: GetCredentialException) {
            Log.e(TAG, "$failureMessage: Failure getting credentials", e)
            e

        } catch (e: GoogleIdTokenParsingException) {
            Log.e(TAG, "$failureMessage: Issue with parsing received GoogleIdToken", e)
            e

        } catch (e: NoCredentialException) {
            Log.e(TAG, "$failureMessage: No credentials found", e)
            e

        } catch (e: GetCredentialCustomException) {
            Log.e(TAG, "$failureMessage: Issue with custom credential request", e)
            e

        } catch (e: GetCredentialCancellationException) {
            Log.e(TAG, "$failureMessage: Sign-in was cancelled", e)
            e
        }
    }

    override suspend fun sendRecoveryEmail(email: String): Result<Unit> {
        return safeTrace("sendRecoveryEmail") {
            auth.sendPasswordResetEmail(email).await()
        }
    }

    override suspend fun createAnonymousAccount(): Result<String> {
        return safeTrace("createAnonymousAccount") {
            val result = auth.signInAnonymously().await()
            result.user?.uid.toString()
        }
    }

    override suspend fun linkAccount(email: String, password: String): Result<Unit> {
        val user = auth.currentUser ?: throw Exception("No user logged in to link")

        return safeTrace(LINK_ACCOUNT_TRACE) {
            try {
                val credential = EmailAuthProvider.getCredential(email, password)
                user.linkWithCredential(credential).await()
            } catch (e: FirebaseAuthUserCollisionException) {
                // Logic for when the email is already taken
            }
        }
    }

    override suspend fun linkAccountWithGoogle(context: Context): Result<Unit> {
        val credentialManager = CredentialManager.create(context)

        val result = credentialManager.getCredential(
            context = context,
            request = request
        )
        val credential = result.credential

        return safeTrace("linkAccountWithGoogle") {
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {

                // Parse the raw data into a usable object
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                // This is the String token Firebase needs
                val idToken = googleIdTokenCredential.idToken

                // 4. Create Firebase Credential and Link
                val firebaseAuthCredential = GoogleAuthProvider.getCredential(idToken, null)

                val user = auth.currentUser ?: throw Exception("No anonymous user found to link")

                user.linkWithCredential(firebaseAuthCredential).await()
            } else {
                throw Exception("Received unexpected credential type")
            }
        }

    }

    override suspend fun deleteAccount(): Result<Unit> {
        return safeTrace("deleteAccount") {
            auth.currentUser!!.delete().await()
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return safeTrace("signOut") {
            if (auth.currentUser!!.isAnonymous) {
                auth.currentUser!!.delete().await()
            }
            auth.signOut()

            // Sign the user back in anonymously.
            createAnonymousAccount()
        }
    }

    companion object {
        private const val LINK_ACCOUNT_TRACE = "linkAccount"
        private const val WEB_CLIENT_ID =
            "423384749464-il4b85up7in6sjjlj2bg4bdr591ev580.apps.googleusercontent.com"
        private const val TAG = "TAG"
    }
}