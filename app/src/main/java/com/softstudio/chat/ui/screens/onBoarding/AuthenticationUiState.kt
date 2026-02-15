package com.softstudio.chat.ui.screens.onBoarding

import android.net.Uri

data class AuthenticationUiState(
    val signInButtonEnableState: Boolean = true,
    val signInButtonLoaderState: Boolean = false,

    val googleSignInFailureMessage: String? = null,
    val googleSignInButtonLoaderState: Boolean = false, // Same used for anonymous login

    val anonymousSignInFailureMessage: String? = null,

    val saveInfoFailureMessage: String? = null,

    val saveInfoLoaderState: Boolean = false,

    val emailSignUpFailureMessage: String? = null,
    val emailSignInFailureMessage: String? = null,

    val imageUploadFailureMessage: String? = null,
    val uploadImageLoaderState: Boolean = false,

    val profileImageUrl: String? = null,
    val profileImageUri: Uri? = null,

    // User Data
    val id: String? = null,
    val email: String? = null,
    val displayName: String? = null,
    val imageUrl: String? = null,
    val bio: String? = null,
    val createdAt: String? = null,
    val providerId: String? = null,
    val isAnonymous: Boolean = true,
    val isOnline: Boolean = false,
    val lastSeen: com.google.firebase.Timestamp? = null
)