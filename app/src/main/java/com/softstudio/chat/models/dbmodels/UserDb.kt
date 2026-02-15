package com.softstudio.chat.models.dbmodels

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.softstudio.chat.models.User

@Entity
data class UserDb(
    @PrimaryKey
    val id: String = "",
    val email: String? = null,
    val displayName: String? = null,
    val imageUrl: String? = null,
    val bio: String = "",
    val createdAt: String = "",
    val providerId: String? = null,
    val isAnonymous: Boolean,
    val isOnline: Boolean = false,
    val lastSeen: com.google.firebase.Timestamp? = null
){
    fun toUser(): User = User(
        id = this.id,
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