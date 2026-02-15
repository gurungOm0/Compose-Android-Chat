package com.softstudio.chat.services.sharedpref

import com.softstudio.chat.models.User

interface SharedPref {
    suspend fun saveUser(user: User)
    suspend fun getUser(): User?
    suspend fun clearUser()
}