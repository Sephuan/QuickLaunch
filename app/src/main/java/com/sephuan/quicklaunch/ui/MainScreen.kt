package com.sephuan.quicklaunch.ui

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sephuan.quicklaunch.R
import com.sephuan.quicklaunch.ui.screens.AboutScreen
import com.sephuan.quicklaunch.ui.screens.AllAppsScreen
import com.sephuan.quicklaunch.ui.screens.CategoryScreen
import com.sephuan.quicklaunch.ui.screens.HomeScreen
import com.sephuan.quicklaunch.ui.screens.SettingsScreen
import com.sephuan.quicklaunch.ui.screens.StatsScreen

sealed class Screen(val route: String, val titleRes: Int, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    data object Home : Screen("home", R.string.home, Icons.Filled.Home, Icons.Outlined.Home)
    data object Category : Screen("category", R.string.categories, Icons.Filled.Category, Icons.Outlined.Category)
    data object AllApps : Screen("all_apps", R.string.app_library, Icons.Filled.Apps, Icons.Outlined.Apps)
    data object Stats : Screen("stats", R.string.stats, Icons.Filled.BarChart, Icons.Outlined.BarChart)
    data object Settings : Screen("settings", R.string.settings, Icons.Filled.Settings, Icons.Outlined.Settings)
    data object About : Screen("about", R.string.about, Icons.Filled.Info, Icons.Filled.Info)
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    val items = listOf(Screen.Home, Screen.Category, Screen.AllApps, Screen.Stats)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute != Screen.Settings.route && currentRoute != Screen.About.route

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val currentDestination = navBackStackEntry?.destination

                    items.forEach { screen ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = stringResource(screen.titleRes)
                                )
                            },
                            label = { Text(stringResource(screen.titleRes)) },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route, enterTransition = { fadeIn(tween(200)) }, exitTransition = { fadeOut(tween(150)) }) { HomeScreen(onSettingsClick = { navController.navigate(Screen.Settings.route) }) }
            composable(Screen.Category.route, enterTransition = { fadeIn(tween(200)) }, exitTransition = { fadeOut(tween(150)) }) { CategoryScreen() }
            composable(Screen.AllApps.route, enterTransition = { fadeIn(tween(200)) }, exitTransition = { fadeOut(tween(150)) }) { AllAppsScreen() }
            composable(Screen.Stats.route, enterTransition = { fadeIn(tween(200)) }, exitTransition = { fadeOut(tween(150)) }) { StatsScreen(onSettingsClick = { navController.navigate(Screen.Settings.route) }) }
            composable(
                Screen.Settings.route,
                enterTransition = { slideInHorizontally(tween(300)) { it } },
                exitTransition = { fadeOut(tween(200)) },
                popEnterTransition = { fadeIn(tween(200)) },
                popExitTransition = { slideOutHorizontally(tween(300)) { it } }
            ) { SettingsScreen(onBack = { navController.popBackStack() }, onAboutClick = { navController.navigate(Screen.About.route) }) }
            composable(
                Screen.About.route,
                enterTransition = { slideInHorizontally(tween(300)) { it } },
                exitTransition = { fadeOut(tween(200)) },
                popEnterTransition = { fadeIn(tween(200)) },
                popExitTransition = { slideOutHorizontally(tween(300)) { it } }
            ) { AboutScreen(onBack = { navController.popBackStack() }) }
        }
    }
}
