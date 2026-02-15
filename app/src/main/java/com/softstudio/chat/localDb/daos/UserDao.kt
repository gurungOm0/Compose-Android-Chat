package com.softstudio.chat.localDb.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.softstudio.chat.models.dbmodels.UserDb
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM UserDb")
    fun getAllUsers(): Flow<List<UserDb>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user: UserDb)

    @Query("SELECT * FROM UserDb WHERE id = :id")
    fun getUser(id: String): Flow<UserDb>

    @Delete suspend fun deleteUser(user: UserDb)
}