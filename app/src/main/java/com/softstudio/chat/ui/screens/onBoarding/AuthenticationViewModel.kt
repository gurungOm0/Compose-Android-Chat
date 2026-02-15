package com.softstudio.chat.ui.screens.onBoarding

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softstudio.chat.localDb.daos.UserDao
import com.softstudio.chat.models.User
import com.softstudio.chat.models.dbmodels.UserDb
import com.softstudio.chat.services.repository.AccountService
import com.softstudio.chat.services.repository.ProfileService
import com.softstudio.chat.services.sharedpref.SharedPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val accountService: AccountService,
    private val profileService: ProfileService,
    private val sharedPref: SharedPref,
    private val dao: UserDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthenticationUiState())
    val uiState: StateFlow<AuthenticationUiState> = _uiState.asStateFlow()

    private suspend inline fun <T> Result<T>.handle(
        crossinline onLoading: (Boolean) -> Unit,
        crossinline onFailure: (Throwable) -> Unit,
        crossinline onSuccess: suspend (T) -> Unit
    ) {
        onLoading(true)
        this.onSuccess {
            onLoading(false)
            onSuccess(it)
        }
        this.onFailure {
            onLoading(false)
            onFailure(it)
        }
    }

    private suspend fun completeAuthFlow(navigate: () -> Unit) {
        saveUserBasicState()
        saveUserToSharedPref()
        navigate()
    }

    private fun updateState(transform: (AuthenticationUiState) -> AuthenticationUiState) {
        _uiState.update { transform(it) }
    }

    private suspend fun saveUserToSharedPref() {
        sharedPref.saveUser(
            User(
                id = _uiState.value.id,
                displayName = _uiState.value.displayName,
                isAnonymous = _uiState.value.isAnonymous
            )
        )
    }

    private suspend fun saveUserBasicState() {
        accountService.getUser().onSuccess { user ->
            updateState {
                it.copy(
                    id = user?.uid,
                    email = user?.email,
                    providerId = user?.providerId
                )
            }
        }
    }

    fun updateUri(uri: Uri) {
        updateState { it.copy(profileImageUri = uri) }
    }

    fun updateName(name: String) {
        updateState { it.copy(displayName = name) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveInfo(navigate: () -> Unit) {
        val state = _uiState.value
        val currentId = state.id ?: accountService.currentUserId
        if (currentId.isEmpty()) {
            updateState { it.copy(saveInfoFailureMessage = "User ID missing.") }
            return
        }
        val user = User(
            id = currentId,
            displayName = state.displayName,
            email = state.email ?: "",
            imageUrl = state.profileImageUrl ?: state.imageUrl ?: "",
            bio = state.bio ?: "",
            createdAt = LocalDate.now().toString(),
            providerId = state.providerId,
            isAnonymous = state.isAnonymous,
            isOnline = state.isOnline,
            lastSeen = state.lastSeen
        )
        viewModelScope.launch {
            updateState { it.copy(saveInfoLoaderState = true) }
            profileService.createOrUpdateProfile(user)
                .handle(
                    onLoading = { onLoading ->
                        updateState { it.copy(saveInfoLoaderState = onLoading) }
                    },
                    onFailure = { error ->
                        updateState { it.copy(saveInfoFailureMessage = error.message) }
                    },
                    onSuccess = {
                        saveToDb(user.toUserDb())
                        navigate()
                    }
                )
        }
    }

    fun onErrorMessageShown() {
        updateState {
            it.copy(
                emailSignInFailureMessage = null,
                emailSignUpFailureMessage = null,
                googleSignInFailureMessage = null,
                imageUploadFailureMessage = null
            )
        }
    }

    private suspend fun saveToDb(user: UserDb) {
        dao.addUser(user)
    }

    fun signUpWithEmailAndPassword(email: String, password: String, navigate: () -> Unit) {
        viewModelScope.launch {
            accountService.signUp(email, password)
                .handle(
                    onLoading = { isLoading ->
                        updateState { it.copy(signInButtonLoaderState = isLoading) }
                    },
                    onFailure = { error ->
                        updateState { it.copy(emailSignUpFailureMessage = error.message) }
                    },
                    onSuccess = {
                        completeAuthFlow(navigate)
                    }
                )
        }
    }

    fun signInWithEmailAndPassword(email: String, password: String, navigate: () -> Unit) {
        viewModelScope.launch {
            accountService.signIn(email, password)
                .handle(
                    onLoading = { isLoading ->
                        updateState { it.copy(signInButtonLoaderState = isLoading) }
                    },
                    onFailure = { error ->
                        updateState { it.copy(emailSignInFailureMessage = error.message) }
                    },
                    onSuccess = {
                        completeAuthFlow(navigate)
                    }
                )
        }
    }

    fun signInWihGoogle(context: Context, navigate: () -> Unit) {
        viewModelScope.launch {
            updateState {
                it.copy(
                    googleSignInButtonLoaderState = true,
                    signInButtonEnableState = false
                )
            }
            accountService.signInWithGoogle(context)
                .handle(
                    onLoading = { isLoading ->
                        updateState {
                            it.copy(
                                googleSignInButtonLoaderState = isLoading,
                                signInButtonEnableState = !isLoading
                            )
                        }
                    },
                    onFailure = { error ->
                        updateState { it.copy(googleSignInFailureMessage = error.message) }

                    },
                    onSuccess = {
                        completeAuthFlow(navigate)
                    }
                )
        }
    }

    fun signInAnonymously(navigate: () -> Unit) {
        viewModelScope.launch {
            updateState {
                it.copy(
                    googleSignInButtonLoaderState = true,
                    signInButtonEnableState = false
                )
            }
            accountService.createAnonymousAccount().onSuccess { uid ->
                updateState {
                    it.copy(
                        id = uid,
                        isAnonymous = true,
                        googleSignInButtonLoaderState = false,
                        signInButtonEnableState = true
                    )
                }
                completeAuthFlow(navigate)
            }
                .onFailure { failure ->
                    updateState {
                        it.copy(
                            anonymousSignInFailureMessage = failure.message,
                            googleSignInButtonLoaderState = false,
                            signInButtonEnableState = true
                        )
                    }
                }
        }
    }

    fun uploadProfileImage() {
        val uri = _uiState.value.profileImageUri ?: return
        viewModelScope.launch {
            profileService.uploadProfilePicture(uri).handle(
                onLoading = { isLoading ->
                    updateState { it.copy(uploadImageLoaderState = isLoading) }
                },
                onFailure = { error ->
                    updateState { it.copy(imageUploadFailureMessage = error.message) }
                },
                onSuccess = { imageUrl ->
                    updateState { it.copy(profileImageUrl = imageUrl) }
                }
            )
        }
    }
}