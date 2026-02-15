package com.softstudio.chat.models.dbmodels

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.softstudio.chat.models.UserProfile

@Entity
data class UserProfileDb(
    @PrimaryKey
    val id: String = "",
    val displayName: String = "",
    val imageUrl: String = "",
    val bio: String = "",
    val phoneNumber: String = "",
    val createdAt: String = "",
    val isOnline: Boolean = false,
    val lastSeen: com.google.firebase.Timestamp? = null
){
    fun toUserProfile(): UserProfile = UserProfile(
        id = this.id,
        displayName = this.displayName,
        imageUrl = this.imageUrl,
        bio = this.bio,
        phoneNumber = this.phoneNumber,
        createdAt = this.createdAt,
        isOnline = this.isOnline,
        lastSeen = this.lastSeen
    )
}
