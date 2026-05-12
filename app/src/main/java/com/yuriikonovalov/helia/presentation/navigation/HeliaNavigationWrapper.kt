package com.yuriikonovalov.helia.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yuriikonovalov.helia.R
import com.yuriikonovalov.helia.designsystem.components.BottomNavigation
import com.yuriikonovalov.helia.designsystem.theme.HeliaTheme
import com.yuriikonovalov.helia.presentation.assistant.AssistantNavigation
import com.yuriikonovalov.helia.presentation.assistant.navigateToAssistantRoute
import com.yuriikonovalov.helia.presentation.auth.authentication.AuthenticationNavigation
import com.yuriikonovalov.helia.presentation.booking.BookingNavigation
import com.yuriikonovalov.helia.presentation.home.HomeNavigation
import com.yuriikonovalov.helia.presentation.profile.ProfileNavigation
import com.yuriikonovalov.helia.presentation.search.SearchNavigation
import com.yuriikonovalov.helia.presentation.welcome.WelcomeNavigation

@Composable
fun HeliaNavigationWrapper(
    startDestination: String,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val destinationAction = rememberTopLevelDestinationNavigationAction(navController)
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    Scaffold(
        modifier = modifier,
        containerColor = HeliaTheme.backgroundColor,
        topBar = {},
        bottomBar = {
            if (currentDestination.isBottomBarVisible()) {
                BottomNavigation(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(vertical = 8.dp),
                    destinations = TopLevelDestination.values().toList(),
                    onNavigateToDestination = destinationAction::navigate,
                    currentDestination = currentDestination
                )
            }
        },
        content = { scaffoldPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                HeliaNavHost(
                    modifier = Modifier.padding(bottom = scaffoldPadding.calculateBottomPadding()),
                    navController = navController,
                    startDestination = startDestination
                )

                // ✅ Floating AI Assistant button (moved from MainActivity)
                val showFab = currentRoute != null &&
                        currentRoute != AssistantNavigation.route &&
                        currentRoute != WelcomeNavigation.route &&
                        currentRoute != AuthenticationNavigation.route

                if (showFab) {
                    FloatingAssistantButton(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .navigationBarsPadding()
                            .padding(end = 20.dp, bottom = 90.dp),
                        onClick = { navController.navigateToAssistantRoute() }
                    )
                }
            }
        }
    )
}

@Composable
private fun FloatingAssistantButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(HeliaTheme.colors.primary500)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(28.dp),
            painter = painterResource(id = R.drawable.ic_info_square_border),
            contentDescription = "AI Assistant",
            tint = HeliaTheme.colors.white
        )
    }
}

private fun NavDestination?.isBottomBarVisible(): Boolean {
    return this?.route in listOf(
        HomeNavigation.route,
        SearchNavigation.route,
        BookingNavigation.route,
        ProfileNavigation.route
    )
}