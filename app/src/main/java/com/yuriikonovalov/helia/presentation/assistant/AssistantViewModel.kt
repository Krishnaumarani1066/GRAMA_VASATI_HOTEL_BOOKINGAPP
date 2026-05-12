package com.yuriikonovalov.helia.presentation.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuriikonovalov.helia.data.api.AnthropicApiService
import com.yuriikonovalov.helia.data.api.AnthropicMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import javax.inject.Inject

@HiltViewModel
class AssistantViewModel @Inject constructor(
    private val apiService: AnthropicApiService
) : ViewModel() {

    private val _state = MutableStateFlow(AssistantUiState())
    val state: StateFlow<AssistantUiState> = _state.asStateFlow()

    fun handleIntent(intent: AssistantIntent) {
        when (intent) {
            is AssistantIntent.SendMessage -> sendMessage(intent.text)
            AssistantIntent.ClearChat -> clearChat()
        }
    }

    @OptIn(InternalSerializationApi::class)
    private fun sendMessage(text: String) {
        if (text.isBlank()) return

        val userMessage = ChatMessage(text = text, isFromUser = true)
        _state.update { it.copy(messages = it.messages + userMessage, isLoading = true, error = null) }

        viewModelScope.launch(Dispatchers.IO) {
            val history = _state.value.messages.map {
                AnthropicMessage(
                    role = if (it.isFromUser) "user" else "assistant",
                    content = it.text
                )
            }

            apiService.sendMessage(history).fold(
                onSuccess = { reply ->
                    val assistantMessage = ChatMessage(text = reply, isFromUser = false)
                    _state.update {
                        it.copy(messages = it.messages + assistantMessage, isLoading = false)
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Unknown error" // ← change this line
                        )
                    }
                }
            )
        }
    }

    private fun clearChat() {
        _state.update { AssistantUiState() }
    }
}