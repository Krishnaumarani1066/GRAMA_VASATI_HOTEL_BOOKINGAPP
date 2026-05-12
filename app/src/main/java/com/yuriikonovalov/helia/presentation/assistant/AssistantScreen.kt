package com.yuriikonovalov.helia.presentation.assistant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yuriikonovalov.helia.R
import com.yuriikonovalov.helia.designsystem.components.TopDestinationNavbar
import com.yuriikonovalov.helia.designsystem.theme.HeliaTheme

@Composable
fun AssistantScreen(
    onDismiss: () -> Unit,
    viewModel: AssistantViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        TopDestinationNavbar(
            modifier = Modifier.padding(horizontal = 24.dp),
            title = "AI Assistant",
            onNavigationClick = { onDismiss() }  // wrap in composable lambda
        )
        if (state.messages.isEmpty()) {
            WelcomeContent(modifier = Modifier.weight(1f))
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.messages, key = { it.id }) { message ->
                    ChatBubble(message = message)
                }
                if (state.isLoading) {
                    item { TypingIndicator() }
                }
                if (state.error != null) {
                    item {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            text = state.error!!,
                            style = HeliaTheme.typography.bodySmallRegular,
                            color = HeliaTheme.colors.error
                        )
                    }
                }
            }
        }

        MessageInputBar(
            isLoading = state.isLoading,
            onSend = { viewModel.handleIntent(AssistantIntent.SendMessage(it)) }
        )
    }
}

@Composable
private fun WelcomeContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(HeliaTheme.colors.primary500.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(40.dp),
                painter = painterResource(id = R.drawable.ic_info_square_border),
                contentDescription = null,
                tint = HeliaTheme.colors.primary500
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = "Hotel AI Assistant",
            style = HeliaTheme.typography.heading4,
            color = if (HeliaTheme.theme.isDark) HeliaTheme.colors.white else HeliaTheme.colors.greyscale900
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = "Ask me anything about hotels,\nrecommendations, or bookings.",
            style = HeliaTheme.typography.bodyMediumRegular,
            color = HeliaTheme.colors.greyscale600,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isFromUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(HeliaTheme.colors.primary500),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(18.dp),
                    painter = painterResource(id = R.drawable.ic_info_square_border),
                    contentDescription = null,
                    tint = HeliaTheme.colors.white
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = if (message.isFromUser) 16.dp else 4.dp,
                        topEnd = if (message.isFromUser) 4.dp else 16.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp
                    )
                )
                .background(
                    if (message.isFromUser) HeliaTheme.colors.primary500
                    else if (HeliaTheme.theme.isDark) HeliaTheme.colors.greyscale800
                    else HeliaTheme.colors.greyscale100
                )
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.text,
                style = HeliaTheme.typography.bodyMediumRegular,
                color = if (message.isFromUser) HeliaTheme.colors.white
                else if (HeliaTheme.theme.isDark) HeliaTheme.colors.white
                else HeliaTheme.colors.greyscale900
            )
        }
    }
}

@Composable
private fun TypingIndicator() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(HeliaTheme.colors.primary500),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(18.dp),
                painter = painterResource(id = R.drawable.ic_info_square_border),
                contentDescription = null,
                tint = HeliaTheme.colors.white
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                .background(
                    if (HeliaTheme.theme.isDark) HeliaTheme.colors.greyscale800
                    else HeliaTheme.colors.greyscale100
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = HeliaTheme.colors.primary500,
                strokeWidth = 2.dp
            )
        }
    }
}

@Composable
private fun MessageInputBar(
    isLoading: Boolean,
    onSend: (String) -> Unit
) {
    var text by rememberSaveable { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = text,
            onValueChange = { text = it },
            placeholder = {
                Text(
                    text = "Ask about hotels...",
                    style = HeliaTheme.typography.bodyMediumRegular,
                    color = HeliaTheme.colors.greyscale500
                )
            },
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = HeliaTheme.colors.primary500,
                unfocusedBorderColor = if (HeliaTheme.theme.isDark)
                    HeliaTheme.colors.greyscale700 else HeliaTheme.colors.greyscale200,
                focusedTextColor = if (HeliaTheme.theme.isDark)
                    HeliaTheme.colors.white else HeliaTheme.colors.greyscale900,
                unfocusedTextColor = if (HeliaTheme.theme.isDark)
                    HeliaTheme.colors.white else HeliaTheme.colors.greyscale900,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = {
                if (text.isNotBlank() && !isLoading) {
                    onSend(text)
                    text = ""
                }
            }),
            maxLines = 3
        )

        IconButton(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (text.isNotBlank() && !isLoading) HeliaTheme.colors.primary500
                    else HeliaTheme.colors.greyscale300
                ),
            onClick = {
                if (text.isNotBlank() && !isLoading) {
                    onSend(text)
                    text = ""
                }
            },
            enabled = text.isNotBlank() && !isLoading
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = R.drawable.ic_logout_border), // replace with send icon
                contentDescription = "Send",
                tint = HeliaTheme.colors.white
            )
        }
    }
}