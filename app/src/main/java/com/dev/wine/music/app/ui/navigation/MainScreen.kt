package com.dev.wine.music.app.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dev.wine.music.app.ui.theme.NavBarBackground
import com.dev.wine.music.app.ui.theme.NavBarDivider
import com.dev.wine.music.app.ui.theme.NavBarInactive
import com.dev.wine.music.app.ui.theme.RecordBlack
import com.dev.wine.music.app.ui.theme.Terracotta
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
                VinylBottomBar(
                    isSelected = { currentDestination.isOn(it) },
                    onSelect = { navController.navigateToTopLevel(it) },
                )
            }
        },
    ) { innerPadding ->
        WineMusicNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

/** Bottom bar from the catalog mockup: outlined icons, dark ink when selected and a terracotta underline. */
@Composable
private fun VinylBottomBar(
    isSelected: (TopLevelDestination) -> Boolean,
    onSelect: (TopLevelDestination) -> Unit,
) {
    // The mockup only defines the light palette; dark mode keeps the theme colors.
    val dark = isSystemInDarkTheme()
    val container = if (dark) MaterialTheme.colorScheme.surfaceContainer else NavBarBackground
    val divider = if (dark) MaterialTheme.colorScheme.outlineVariant else NavBarDivider
    val active = if (dark) MaterialTheme.colorScheme.onSurface else RecordBlack
    val inactive = if (dark) MaterialTheme.colorScheme.onSurfaceVariant else NavBarInactive
    val itemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = active,
        selectedTextColor = active,
        unselectedIconColor = inactive,
        unselectedTextColor = inactive,
        // No pill behind the icon: the underline below the label marks the selected tab.
        indicatorColor = Color.Transparent,
    )
    Column {
        HorizontalDivider(color = divider)
        NavigationBar(containerColor = container) {
            TopLevelDestination.entries.forEach { destination ->
                val selected = isSelected(destination)
                NavigationBarItem(
                    selected = selected,
                    onClick = { onSelect(destination) },
                    icon = { Icon(painterResource(destination.iconRes), contentDescription = null) },
                    label = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(destination.labelRes),
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            )
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .size(width = 32.dp, height = 3.dp)
                                    .background(if (selected) Terracotta else Color.Transparent),
                            )
                        }
                    },
                    alwaysShowLabel = true,
                    colors = itemColors,
                    modifier = Modifier.testTag("nav_${destination.name.lowercase()}"),
                )
            }
        }
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
