package dev.oruizp.expensetracker.android.features.home.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import dev.oruizp.expensetracker.android.core.theme.CategoryBills
import dev.oruizp.expensetracker.android.core.theme.CategoryEntertainment
import dev.oruizp.expensetracker.android.core.theme.CategoryFood
import dev.oruizp.expensetracker.android.core.theme.CategoryOther
import dev.oruizp.expensetracker.android.core.theme.CategoryShopping
import dev.oruizp.expensetracker.android.core.theme.CategoryTransport
import dev.oruizp.expensetracker.android.core.theme.ExpenseTrackerTheme
import dev.oruizp.expensetracker.android.domain.models.ExpenseDomain
import dev.oruizp.expensetracker.android.features.home.models.ExpenseCategoryUi
import dev.oruizp.expensetracker.android.features.home.models.ExpenseUi
import dev.oruizp.expensetracker.android.features.home.models.HomeDetails
import dev.oruizp.expensetracker.android.features.home.state.BudgetCategory
import dev.oruizp.expensetracker.android.features.home.state.ExpensesUiState
import dev.oruizp.expensetracker.android.features.home.state.TotalSpentUiState
import dev.oruizp.expensetracker.android.features.home.viewmodel.ExpenseViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeScreen(
    expenseViewModel : ExpenseViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val expenses by expenseViewModel.getExpensesUiState.collectAsState()
    val totalSpent by expenseViewModel.totalSpentUiState.collectAsState()

    HomeScreenContent(
        homeDetails = HomeDetails(
            totalSpent = totalSpent,
            expenses = expenses,
            percentageOffset = 0.0
        ),
        onAddExpense = { expenseViewModel.addExpense(it) },
        onNotificationsClick = {
            Toast.makeText(context, "Notifications Clicked", Toast.LENGTH_SHORT).show()
        },
        onSettingsClick = {
            Toast.makeText(context, "Settings Clicked", Toast.LENGTH_SHORT).show()
        },
        onSeeAllTransactionsClick = {
            Toast.makeText(context, "See All Transactions Clicked", Toast.LENGTH_SHORT).show()
        },
        onBudgetOverviewClick = {
            Toast.makeText(context, "Budget Overview Clicked", Toast.LENGTH_SHORT).show()
        },
        onTransactionClick = { id ->
            Toast.makeText(context, "Transaction $id Clicked", Toast.LENGTH_SHORT).show()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    homeDetails: HomeDetails,
    budgetOverViews: List<BudgetCategory> = emptyList(),
    onAddExpense: (ExpenseDomain) -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onSeeAllTransactionsClick: () -> Unit = {},
    onBudgetOverviewClick: () -> Unit = {},
    onTransactionClick: (Long) -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expense Tracker") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = onNotificationsClick) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showBottomSheet = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TotalSpentCard(totalSpent = homeDetails.totalSpent, 0.0)

            BudgetOverviewCard(
                expenses = budgetOverViews,
                onClick = onBudgetOverviewClick
            )

            RecentTransactionsView(
                expenses = homeDetails.expenses,
                onAllTransactionsClick = onSeeAllTransactionsClick,
                onItemClick = onTransactionClick
            )
        }

        if (showBottomSheet) {
            AddExpenseSheet(
                sheetState = sheetState,
                onDismiss = { showBottomSheet = false },
                onSave = { expense ->
                    onAddExpense(expense)
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                        }
                    }
                }
            )
        }
    }
}

fun getCategoryColor(category: ExpenseCategoryUi): Color {
    return when (category) {
        ExpenseCategoryUi.FOOD -> CategoryFood
        ExpenseCategoryUi.TRANSPORT -> CategoryTransport
        ExpenseCategoryUi.SHOPPING -> CategoryShopping
        ExpenseCategoryUi.BILLS -> CategoryBills
        ExpenseCategoryUi.ENTERTAINMENT -> CategoryEntertainment
        ExpenseCategoryUi.OTHER -> CategoryOther
    }
}

fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
    return format.format(amount)
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val now = System.currentTimeMillis()
    val yesterday = now - 24 * 60 * 60 * 1000
    val sampleExpense = listOf(
        ExpenseUi(1,
            "Groceries",
            50.0,

            ExpenseCategoryUi.FOOD,
            yesterday
        ),
        ExpenseUi(2,
            "Bus Fare",
            2.5,
            ExpenseCategoryUi.TRANSPORT,
            yesterday
        ),
        ExpenseUi(3,
            "Movie",
            15.0,
            ExpenseCategoryUi.ENTERTAINMENT,
            yesterday
        ),
        ExpenseUi(4,
            "Rent",
            1200.0,
            ExpenseCategoryUi.BILLS,
            yesterday
        )
    )
    val sampleBudgetCategory = listOf(
        BudgetCategory("Food", 50.0f, 100.0f, CategoryFood),
        BudgetCategory("Transport", 20.0f, 100.0f, CategoryTransport),
        BudgetCategory("Shopping", 30.0f, 100.0f, CategoryShopping),
        BudgetCategory("Bills", 10.0f, 100.0f, CategoryBills),
        BudgetCategory("Entertainment", 15.0f, 100.0f, CategoryEntertainment),
        BudgetCategory("Other", 10.0f, 100.0f, CategoryOther)
    )
    ExpenseTrackerTheme {
        HomeScreenContent(
            homeDetails = HomeDetails(
                totalSpent = TotalSpentUiState.Success("1250.00"),
                expenses = ExpensesUiState.Success(sampleExpense),
                percentageOffset = 0.0
            ),
            budgetOverViews = sampleBudgetCategory
        )
    }
}
