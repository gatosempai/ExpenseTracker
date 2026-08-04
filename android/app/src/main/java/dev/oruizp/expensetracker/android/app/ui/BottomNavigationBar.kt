package dev.oruizp.expensetracker.android.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import dev.oruizp.expensetracker.android.features.home.ui.HomeScreen

@Composable
fun BottomNavigationBar() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.parent?.route ?: currentBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                val items = listOf(
                    TopLevelRoute.Home,
                    TopLevelRoute.Transactions,
                    TopLevelRoute.Budgets,
                    TopLevelRoute.Reports,
                    TopLevelRoute.Settings,
                )
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(imageVector = item.icon, contentDescription = null) },
                        label = { Text(stringResource(item.labelRes)) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TopLevelRoute.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            homeGraph(navController)
            transactionsGraph(navController)
            budgetsGraph(navController)
            reportsGraph(navController)
            settingsGraph(navController)
        }
    }
}

fun NavGraphBuilder.homeGraph(navController: NavController) {
    navigation(
        startDestination = "home_screen",
        route = TopLevelRoute.Home.route
    ) {
        composable("home_screen") {
            HomeScreen()
        }
    }
}

fun NavGraphBuilder.transactionsGraph(navController: NavController) {
    navigation(
        startDestination = "transactions_screen",
        route = TopLevelRoute.Transactions.route
    ) {
        composable("transactions_screen") {
            Text("Transactions Screen")
        }
    }
}

fun NavGraphBuilder.budgetsGraph(navController: NavController) {
    navigation(
        startDestination = "budgets_screen",
        route = TopLevelRoute.Budgets.route
    ) {
        composable("budgets_screen") {
            Text("Budgets Screen")
        }
    }
}

fun NavGraphBuilder.reportsGraph(navController: NavController) {
    navigation(
        startDestination = "reports_screen",
        route = TopLevelRoute.Reports.route
    ) {
        composable("reports_screen") {
            Text("Reports Screen")
        }
    }
}

fun NavGraphBuilder.settingsGraph(navController: NavController) {
    navigation(
        startDestination = "settings_screen",
        route = TopLevelRoute.Settings.route
    ) {
        composable("settings_screen") {
            Text("Settings Screen")
        }
    }
}
