package com.softstudio.chat.services.sharedpref

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import com.softstudio.chat.models.User
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPrefImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
): SharedPref {
    private val sharedPreference = context.getSharedPreferences("User",MODE_PRIVATE)

    override suspend fun saveUser(user: User){
        sharedPreference.edit {
            putString("UserId",user.id)
            putString("Name",user.displayName)
            putBoolean("isAnonymous",user.isAnonymous)
        }
    }

    override suspend fun getUser(): User {
        return User(
            id = sharedPreference.getString("id",""),
            displayName = sharedPreference.getString("name",""),
            isAnonymous = sharedPreference.getBoolean("isAnonymous",true)
        )
    }

    override suspend fun clearUser() {
        sharedPreference.edit { clear().apply() }
    }

}