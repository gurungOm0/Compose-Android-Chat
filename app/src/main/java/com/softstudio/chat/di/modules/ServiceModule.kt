package com.softstudio.chat.di.modules

import com.softstudio.chat.services.repository.AccountService
import com.softstudio.chat.services.repository.MessageService
import com.softstudio.chat.services.repository.ProfileService
import com.softstudio.chat.services.repository.implementations.AccountServiceImpl
import com.softstudio.chat.services.repository.implementations.MessageServiceImpl
import com.softstudio.chat.services.repository.implementations.ProfileServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds
    abstract fun bindAccountServiceImpl(impl: AccountServiceImpl): AccountService

    @Binds
    abstract fun bindMessageService(impl: MessageServiceImpl): MessageService

    @Binds
    abstract fun bindProfileService(impl: ProfileServiceImpl): ProfileService

}