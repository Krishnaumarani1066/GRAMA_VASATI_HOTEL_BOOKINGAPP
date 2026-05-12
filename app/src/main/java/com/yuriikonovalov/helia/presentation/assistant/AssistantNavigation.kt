package com.yuriikonovalov.helia.presentation.assistant

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

object AssistantNavigation {
    const val route = "assistant"
}

fun NavGraphBuilder.assistantRoute(
    onDismiss: () -> Unit
) {
    composable(route = AssistantNavigation.route) {
        AssistantScreen(onDismiss = onDismiss)
    }
}

fun NavHostController.navigateToAssistantRoute() {
    navigate(route = AssistantNavigation.route)
}