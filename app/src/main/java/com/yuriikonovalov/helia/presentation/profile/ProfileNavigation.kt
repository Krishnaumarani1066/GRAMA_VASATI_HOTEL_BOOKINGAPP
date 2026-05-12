package com.yuriikonovalov.helia.presentation.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

object ProfileNavigation {
    const val route = "profile"
}

fun NavGraphBuilder.profileRoute(
    onEditProfileClick: () -> Unit,
    onSecurityClick: () -> Unit,
    onPaymentClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onHelpClick: () -> Unit,
) {
    composable(route = ProfileNavigation.route) {
        ProfileScreen(
            onEditProfileClick = onEditProfileClick,
            onSecurityClick = onSecurityClick,
            onPaymentClick = onPaymentClick,
            onNotificationsClick = onNotificationsClick,
            onHelpClick = onHelpClick,
        )
    }
}

fun NavHostController.navigateToProfileRoute(navOptions: NavOptions? = null) {
    navigate(route = ProfileNavigation.route, navOptions = navOptions)
}