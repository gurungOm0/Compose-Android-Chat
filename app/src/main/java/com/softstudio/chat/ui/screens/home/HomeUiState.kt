package com.softstudio.chat.ui.screens.home

import com.softstudio.chat.models.UserProfile
import com.softstudio.chat.models.dbmodels.ConversationDb
import com.softstudio.chat.models.dbmodels.UserDb

data class HomeUiState(
    val profileImage: String? = null,
    val profileImageState : Boolean = false,
    val conversations: List<ConversationDb> = emptyList(),

    val searchQuery: String = "",
    val isSearchMode: Boolean = false,
    val searchResults: List<UserProfile> = emptyList(),
    val isSearching: Boolean = false,

    val remoteConversationErrorMessage: String? = null,

    val currentProfile: UserDb? = null,
    val isAnonymous: Boolean = currentProfile?.isAnonymous ?: true
)
