package com.yuriikonovalov.helia.presentation.assistant

sealed interface AssistantIntent {
    data class SendMessage(val text: String) : AssistantIntent
    object ClearChat : AssistantIntent
}