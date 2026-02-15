package com.softstudio.chat.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.softstudio.chat.models.UserProfile
import com.softstudio.chat.models.dbmodels.ConversationDb
import com.softstudio.chat.ui.theme.ChatTheme
import com.softstudio.chat.util.formatChatTimestamp
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeUiPreview() {
    val state = HomeUiState()
    ChatTheme {
        HomeUi(
            state = state,
            drawerState = DrawerState(DrawerValue.Closed),
            onMenuClick = {},
            onUserClick = {},
            onQueryChange = {},
            onSearchToggle = {},
            onProfileClick = {},
            onConversationClick = {}
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Home(
    navHostController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val state by viewModel.uiState.collectAsState()
    HomeUi(
        state = state,
        drawerState = drawerState,
        onMenuClick = {
            scope.launch { drawerState.open() }
        },
        onProfileClick = {
            // TODO: Navigate to Profile
            scope.launch { drawerState.close() }
        },
        onUserClick = { user ->
            viewModel.onUserClick(user)
        },
        onSearchToggle = { enabled ->
            scope.launch {
                drawerState.close()
            }
            viewModel.toggleSearchMode(enabled)
        },
        onQueryChange = { query ->
            viewModel.onSearchQueryChange(query)
        },
        onConversationClick = { id ->
            scope.launch { drawerState.close() }
            viewModel.onConversationClick(id)
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeUi(
    state: HomeUiState,
    drawerState: DrawerState,
    onMenuClick: () -> Unit,
    onProfileClick: () -> Unit,
    onUserClick: (UserProfile) -> Unit,
    onSearchToggle: (Boolean) -> Unit,
    onQueryChange: (String) -> Unit,
    onConversationClick: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerContent = {
            Drawer(
                onProfileClick = onProfileClick,
                onSettingsClick = {
                    //TODO: Navigate to Settings
                    scope.launch { drawerState.close() }
                },
                onLogoutClick = {
                    //TODO: Logout
                    scope.launch { drawerState.close() }
                }
            )
        },
        drawerState = drawerState,
        content = {
            Scaffold(
                modifier = Modifier,
                topBar = {
                    TopBar(
                        state = state,
                        onMenuClick = onMenuClick,
                        onSearchToggle = onSearchToggle,
                        onQueryChange = onQueryChange
                    )
                }
            ) { innerPadding ->
                if (state.isSearchMode) {
                    LazyColumn(modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)) {
                        items(state.searchResults) { user ->
                            UserSearchItem(user = user, onClick = { onUserClick(user) })
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        items(state.conversations) { conversation ->
                            Conversation(
                                state = state,
                                conversation = conversation,
                                onClick = { onConversationClick(conversation.conversationId) }
                            )
                        }
                    }
                }
            }
        }
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    state: HomeUiState,
    onMenuClick: () -> Unit,
    onSearchToggle: (Boolean) -> Unit,
    onQueryChange: (String) -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            if (state.isSearchMode) {
                TextField(
                    value = state.searchQuery,
                    onValueChange = onQueryChange,
                    placeholder = { Text("Search Users...") },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text("CHAT", style = MaterialTheme.typography.labelLarge)
            }
        },
        navigationIcon = {
            IconButton(
                onClick = { if (state.isSearchMode) onSearchToggle(false) else onMenuClick() }
            ) {
                Icon(
                    imageVector = if (state.isSearchMode) Icons.AutoMirrored.Filled.ArrowBack else Icons.Default.Menu,
                    contentDescription = null
                )
            }
        },
        actions = {
            if (!state.isSearchMode) {
                IconButton(onClick = { onSearchToggle(true) }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            }
        }
    )
}

@Composable
fun UserSearchItem(user: UserProfile, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = user.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        Column(
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(text = user.displayName, style = MaterialTheme.typography.titleMedium)
            Text(text = user.bio, style = MaterialTheme.typography.bodySmall, maxLines = 1)
        }
    }
}

@Composable
fun Drawer(
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    ModalDrawerSheet {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text("CHAT", style = MaterialTheme.typography.labelLarge)
        }
        Spacer(modifier = Modifier.height(16.dp))
        NavigationDrawerItem(
            label = { Text("Profile") },
            selected = false,
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            onClick = onProfileClick,
        )
        NavigationDrawerItem(
            label = { Text("Settings") },
            selected = false,
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            onClick = onSettingsClick
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        NavigationDrawerItem(
            label = { Text("Logout") },
            selected = false,
            icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) },
            onClick = onLogoutClick
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ConversationPreview() {
    val state = HomeUiState()
    val conversation = ConversationDb(
        "",
        listOf(),
        "",
        "Aady",
        null,
        999985400000,
        1,
        false,
        "Hello !"
    )
    ChatTheme {
        Conversation(state, conversation) { }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Conversation(
    state: HomeUiState,
    conversation: ConversationDb,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 88.dp)
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(68.dp),
            contentAlignment = Alignment.Center
        ) {
            if (!conversation.avatarUrl.isNullOrBlank()) {
                AsyncImage(
                    model = conversation.avatarUrl,
                    contentDescription = "Open Conversation",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Open Conversation",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
                        .clip(CircleShape)
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = conversation.conversationName ?: "Unknown",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = conversation.lastMessage ?: "...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Column(
            modifier = Modifier
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = formatChatTimestamp(conversation.lastMessageTimestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (conversation.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (conversation.unreadCount > 99) "99+" else conversation.unreadCount.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(28.dp))
            }

        }
    }
}