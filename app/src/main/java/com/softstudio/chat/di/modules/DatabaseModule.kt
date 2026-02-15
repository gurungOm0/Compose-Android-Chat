package com.softstudio.chat.di.modules

import android.content.Context
import androidx.room.Room
import com.softstudio.chat.localDb.AppDatabase
import com.softstudio.chat.localDb.daos.ConversationDao
import com.softstudio.chat.localDb.daos.MessagesDao
import com.softstudio.chat.localDb.daos.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context) =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "chat.db").build()

    @Provides
    fun providesConversationDao(db: AppDatabase): ConversationDao = db.conversationDao()

    @Provides
    fun providesMessageDao(db: AppDatabase): MessagesDao = db.messagesDao()

    @Provides
    fun providesUserDao(db: AppDatabase): UserDao = db.userDao()
}