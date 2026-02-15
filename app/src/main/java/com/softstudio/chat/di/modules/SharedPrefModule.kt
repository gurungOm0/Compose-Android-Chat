package com.softstudio.chat.di.modules

import com.softstudio.chat.services.sharedpref.SharedPref
import com.softstudio.chat.services.sharedpref.SharedPrefImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SharedPrefModule {
    @Binds
    abstract fun bindSharedPref(impl: SharedPrefImpl): SharedPref
}