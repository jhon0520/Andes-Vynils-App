package com.dev.wine.music.app.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/** App shell: bottom bar for the top-level tabs plus the navigation host. */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    sessionViewModel: SessionViewModel = hiltViewModel(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val showBottomBar = TopLevelDestination.entries.any { currentDestination.isOn(it) }
    val session by sessionViewModel.session.collectAsStateWithLifecycle()

    // If Android killed the process, Navigation restores the screens but the in-memory session is gone.
    // Send the user back to the onboarding, clearing the back stack.
    LaunchedEffect(session, currentDestination) {
        if (session == null && currentDestination != null && !currentDestination.isInOnboarding()) {
            navController.navigate(OnboardingGraph) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    Scaffold(
        // Exposes every testTag as a resource id so Kraken (Appium) can find it.
        modifier = Modifier
            .fillMaxSize()
            .semantics { testTagsAsResourceId = true },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    TopLevelDestination.entries.forEach { destination ->
                        NavigationBarItem(
                            selected = currentDestination.isOn(destination),
                            onClick = { navController.navigateToTopLevel(destination) },
                            icon = { Icon(destination.icon, contentDescription = null) },
                            label = { Text(stringResource(destination.labelRes)) },
                            modifier = Modifier.testTag("nav_${destination.name.lowercase()}"),
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        WineMusicNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

private fun NavDestination?.isOn(destination: TopLevelDestination): Boolean =
    this?.hierarchy?.any { it.hasRoute(destination.route::class) } == true

private fun NavDestination.isInOnboarding(): Boolean =
    hierarchy.any { it.hasRoute(OnboardingGraph::class) }

private fun NavHostController.navigateToTopLevel(destination: TopLevelDestination) {
    navigate(destination.route) {
        // The albums screen is the root once the onboarding is gone. Keep one copy of each tab and its scroll position.
        popUpTo<AlbumsRoute> { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
