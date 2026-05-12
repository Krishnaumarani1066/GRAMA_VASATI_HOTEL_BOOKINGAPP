package com.yuriikonovalov.helia.presentation.booking

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

object BookingNavigation {
    const val route = "booking"
}

fun NavGraphBuilder.bookingRoute(
    onViewTicketClick: (hotelId: String) -> Unit = {}
) {
    composable(route = BookingNavigation.route) {
        BookingScreen(
            onViewTicketClick = onViewTicketClick
        )
    }
}

fun NavHostController.navigateToBookingRoute(navOptions: NavOptions? = null) {
    navigate(route = BookingNavigation.route, navOptions = navOptions)
}