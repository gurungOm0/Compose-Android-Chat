package com.softstudio.chat.localDb.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.softstudio.chat.models.dbmodels.UserProfileDb
import kotlinx.coroutines.flow.Flow


@Dao
interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createProfile(user: UserProfileDb)

    @Query("SELECT * FROM UserProfileDb")
    fun getUserProfile(): Flow<UserProfileDb>

    @Delete suspend fun deleteUserProfile(user: UserProfileDb)
}