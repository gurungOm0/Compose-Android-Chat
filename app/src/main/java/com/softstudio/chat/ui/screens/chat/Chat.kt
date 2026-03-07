package com.softstudio.chat.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.softstudio.chat.models.MessageStatus
import com.softstudio.chat.models.MessageType
import com.softstudio.chat.models.dbmodels.MessageDb
import com.softstudio.chat.navigation.ProfileDes
import com.softstudio.chat.ui.theme.ChatTheme

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChatUiPreview() {
    val messages = listOf(
        MessageDb(
            messageId = "5",
            text = "Ummm..",
            senderId = "2",
            receiverId = "1",
            conversationId = "",
            imageUrl = "",
            videoUrl = "",
            messageType = MessageType.TEXT,
            messageStatus = MessageStatus.SENT,
        ),
        MessageDb(
            messageId = "4",
            text = "Where have you been ?",
            senderId = "1",
            receiverId = "2",
            conversationId = "",
            imageUrl = "",
            videoUrl = "",
            messageType = MessageType.TEXT,
            messageStatus = MessageStatus.SENT,
        ),
        MessageDb(
            messageId = "3",
            text = "How are you ?",
            senderId = "1",
            receiverId = "2",
            conversationId = "",
            imageUrl = "",
            videoUrl = "",
            messageType = MessageType.TEXT,
            messageStatus = MessageStatus.SENT,
        ),
        MessageDb(
            messageId = "2",
            text = "Hi",
            senderId = "2",
            receiverId = "1",
            conversationId = "",
            imageUrl = "",
            videoUrl = "",
            messageType = MessageType.TEXT,
            messageStatus = MessageStatus.SENT,
        ),
        MessageDb(
            messageId = "1",
            text = "Hello",
            senderId = "1",
            receiverId = "2",
            conversationId = "",
            imageUrl = "",
            videoUrl = "",
            messageType = MessageType.TEXT,
            messageStatus = MessageStatus.SENT,
        )
    )
    val state = ChatUiState(currentUserId = "2", conversationName = "Chat", messages = messages)
    ChatTheme {
        ChatUi(
            state = state,
            onProfileClick = {},
            onBackClick = {},
            conversationId = "12"
        )
    }
}

@Composable
fun Chat(
    navHostController: NavHostController,
    viewModel: ChatViewModel = hiltViewModel(),
    conversationId: String
) {
    val state by viewModel.uiState.collectAsState()
    ChatUi(
        state = state,
        conversationId = conversationId,
        onProfileClick = { navHostController.navigate(ProfileDes.route) },
        onBackClick = { navHostController.popBackStack() }
    )
}

@Composable
fun ChatUi(
    state: ChatUiState,
    conversationId: String,
    onProfileClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        ProfileBar(
            image = state.conversationImage,
            conversationName = state.conversationName ?: "Loading...", /*TODO: make sure*/
            onProfileClick = onProfileClick/*TODO: Go to Profile*/,
            onBackClick = onBackClick/*TODO: Go Back*/
        )
        ChatBody(
            messages = state.messages ?: emptyList(),
            currentUserId = state.currentUserId ?: "",
            conversationId = conversationId
        )
    }
}

@Composable
fun ProfileBar(
    image: String? = null,
    conversationName: String,
    onProfileClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onBackClick()/*TODO: Go Back*/ }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Go Back",
                modifier = Modifier
            )
        }
        Row(
            modifier = Modifier
                .clickable(onClick = { onProfileClick()/*TODO: Go to Profile*/ })
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = image,
                contentDescription = "Profile Picture",
                placeholder = rememberVectorPainter(Icons.Default.AccountCircle),
                modifier = Modifier
                    .size(54.dp)
                    .padding(2.dp)
                    .clip(CircleShape)
            )
            Text(
                text = conversationName,
                style = MaterialTheme.typography.titleSmall
            )
        }
        IconButton(
            onClick = {/*TODO: More Options*/ },
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More Options",
            )
        }
    }
}

@Composable
fun ChatBody(messages: List<MessageDb>, currentUserId: String, conversationId: String) {
    val textFieldState = rememberTextFieldState()
    val interactionSource = remember { MutableInteractionSource() }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true,
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = messages,
                key = { message -> message.messageId }
            ) { message ->
                ChatBubble(
                    message = message,
                    currentUserId = currentUserId,
                    userProfileUrl = ""
                )
            }
        }
        Surface(
            tonalElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            BasicTextField(
                state = textFieldState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .navigationBarsPadding(),
                lineLimits = TextFieldLineLimits.MultiLine(1,5),
                decorator = { innerTextField ->
                    TextFieldDefaults.DecorationBox(
                        value = textFieldState.text.toString(),
                        innerTextField = innerTextField,
                        enabled = true,
                        singleLine = false,
                        placeholder = {
                                    Text("Type a message...")
                        },
                        visualTransformation = VisualTransformation.None,
                        interactionSource = interactionSource,
                        trailingIcon = {
                            val hasText = textFieldState.text.isNotBlank()
                                IconButton(
                                    onClick = {
                                        if (hasText) {
                                            /*TODO: Send message*/
                                            textFieldState.edit {
                                                replace(0, length, "")
                                            }
                                        }
                                    },
                                    enabled = hasText
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "Send",
                                        tint = if (hasText) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.outline
                                    )
                                }
                            },
                        container = {
                            TextFieldDefaults.Container(
                                enabled = true,
                                isError = false,
                                interactionSource = interactionSource,
                                colors = TextFieldDefaults.colors(),
                                shape = MaterialTheme.shapes.extraLarge
                            )
                        },
                    )
                }
            )
        }
    }
}


@Composable
fun ChatBubble(message: MessageDb, currentUserId: String, userProfileUrl: String) {
    val isMe = message.senderId == currentUserId
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) {
            Alignment.End
        } else {
            Alignment.Start
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (message.senderId != currentUserId) {
                Arrangement.Start
            } else {
                Arrangement.End
            }
        ) {
            if (!isMe) {
                AsyncImage(
                    model = userProfileUrl,
                    contentDescription = "Profile Picture",
                    placeholder = rememberVectorPainter(Icons.Default.AccountCircle),
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                )
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .background(
                        color = if (isMe) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = message.text,
                    color = if (isMe) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
        if (isMe) {
            val statusIcon = when (message.messageStatus) {
                MessageStatus.SENT -> Icons.Default.Done
                MessageStatus.PENDING -> Icons.Default.AccessTime
                MessageStatus.SEEN -> Icons.Default.DoneAll
                else -> Icons.Default.Error
            }
            Icon(
                imageVector = statusIcon,
                contentDescription = "Message Status",
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .size(16.dp)
                    .align(alignment = Alignment.End),
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}