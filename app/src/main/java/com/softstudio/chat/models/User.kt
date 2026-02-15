package com.softstudio.chat.models

import com.softstudio.chat.models.dbmodels.UserDb

data class  User(
    val id: String? = null,
    val displayName: String? = null,
    val email: String? = null,
    val imageUrl: String? = null,
    val bio: String = "",
    val createdAt: String = "",
    val providerId: String? = null,
    val isAnonymous: Boolean = true,
    val isOnline: Boolean = false,
    val lastSeen: com.google.firebase.Timestamp? = null
){
    fun toUserDb(): UserDb = UserDb(
        id = this.id!!,
        email = this.email,
        displayName = this.displayName,
        imageUrl = this.imageUrl,
        bio = this.bio,
        createdAt = this.createdAt,
        providerId = this.providerId,
        isAnonymous = this.isAnonymous,
        isOnline = this.isOnline,
        lastSeen = this.lastSeen
    )
}