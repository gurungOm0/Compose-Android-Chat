package com.softstudio.chat.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softstudio.chat.models.UserProfile
import com.softstudio.chat.services.repository.ChatRepository
import com.softstudio.chat.services.repository.ProfileService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val repository: ChatRepository,
    val profileService: ProfileService
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val conversations = repository.getConversationsLocal()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.getConversationsLocal().collect { list ->
                _uiState.update { it.copy(conversations = list) }
            }
        }
        syncRemoteConversation()
    }

    fun syncRemoteConversation(){
        viewModelScope.launch {
            repository.syncConversations().collect { result ->
                result.onFailure { error ->
                    _uiState.update { it.copy(remoteConversationErrorMessage = error.message) }
                }
            }
        }
    }

    fun initConversations(){
        _uiState.update { it.copy(conversations = conversations.value) }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.length >=2){
            performSearch(query)
        }else{
            _uiState.update { it.copy(searchResults = emptyList()) }
        }
    }

    fun toggleSearchMode(enabled: Boolean) {
        _uiState.update {
            it.copy(
                isSearchMode = enabled,
                searchQuery = if (!enabled) "" else it.searchQuery,
                searchResults = if (!enabled) emptyList() else it.searchResults
            )
        }
    }

    fun performSearch(query: String){
        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true) }
            profileService.searchUsers(query).onSuccess { users ->
                _uiState.update { it.copy(searchResults = users, isSearching = false) }
            }.onFailure {
                _uiState.update { it.copy(isSearching = false) }
            }
        }
    }

    fun onUserClick(user: UserProfile){
        // TODO: Chat screen with user
    }

    fun onConversationClick(id: String){
        // TODO: Opens Conversation
    }

}