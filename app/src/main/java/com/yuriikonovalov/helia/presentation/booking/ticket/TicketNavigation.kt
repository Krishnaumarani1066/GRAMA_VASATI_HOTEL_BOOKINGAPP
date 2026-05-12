package com.yuriikonovalov.helia.presentation.booking.ticket

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

object TicketNavigation {
    const val hotelIdArg = "hotelId"
    const val route = "ticket/{$hotelIdArg}"

    fun createRoute(hotelId: String) = "ticket/$hotelId"
}

fun NavGraphBuilder.ticketRoute(
    onNavigateClick: () -> Unit
) {
    composable(
        route = TicketNavigation.route,
        arguments = listOf(
            navArgument(TicketNavigation.hotelIdArg) { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val hotelId = backStackEntry.arguments?.getString(TicketNavigation.hotelIdArg) ?: ""
        TicketScreen(
            hotelId = hotelId,
            onNavigateClick = onNavigateClick
        )
    }
}

fun NavHostController.navigateToTicketRoute(hotelId: String, navOptions: NavOptions? = null) {
    navigate(route = TicketNavigation.createRoute(hotelId), navOptions)
}