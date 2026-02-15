package com.softstudio.chat.localDb

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.softstudio.chat.localDb.daos.ConversationDao
import com.softstudio.chat.localDb.daos.MessagesDao
import com.softstudio.chat.localDb.daos.UserDao
import com.softstudio.chat.localDb.daos.UserProfileDao
import com.softstudio.chat.models.dbmodels.ConversationDb
import com.softstudio.chat.models.dbmodels.MessageDb
import com.softstudio.chat.models.dbmodels.UserDb
import com.softstudio.chat.models.dbmodels.UserProfileDb

@Database(entities = [UserProfileDb::class,UserDb::class, ConversationDb::class, MessageDb::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun userDao(): UserDao
    abstract fun conversationDao(): ConversationDao
    abstract fun messagesDao(): MessagesDao
}