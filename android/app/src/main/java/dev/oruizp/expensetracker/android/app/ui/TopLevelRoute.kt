package dev.oruizp.expensetracker.android.app.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.ui.graphics.vector.ImageVector
import dev.oruizp.expensetracker.R

sealed class TopLevelRoute(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector
) {
    object Home : TopLevelRoute("home_graph", R.string.nav_home, Icons.Default.Home)
    object Transactions : TopLevelRoute("transactions_graph", R.string.nav_transactions, Icons.Default.SwapHoriz)
    object Budgets : TopLevelRoute("budgets_graph", R.string.nav_budgets, Icons.Default.AccountBalanceWallet)
    object Reports : TopLevelRoute("reports_graph", R.string.nav_reports, Icons.Default.PieChart)
    object Settings : TopLevelRoute("settings_graph", R.string.nav_settings, Icons.Default.Settings)
}
