package dev.oruizp.expensetracker.android.features.home.ui

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
import androidx.compose.ui.tooling.preview.Preview
import dev.oruizp.expensetracker.android.core.theme.CategoryBills
import dev.oruizp.expensetracker.android.core.theme.CategoryEntertainment
import dev.oruizp.expensetracker.android.core.theme.CategoryFood
import dev.oruizp.expensetracker.android.core.theme.CategoryOther
import dev.oruizp.expensetracker.android.core.theme.CategoryShopping
import dev.oruizp.expensetracker.android.core.theme.CategoryTransport
import dev.oruizp.expensetracker.android.core.theme.ExpenseTrackerTheme
import dev.oruizp.expensetracker.android.data.local.ExpenseCategory
import dev.oruizp.expensetracker.android.domain.models.Expense
import dev.oruizp.expensetracker.android.features.home.data.BudgetCategory
import dev.oruizp.expensetracker.android.features.home.data.TotalSpentUiState
import dev.oruizp.expensetracker.android.features.home.viewmodel.ExpenseViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeScreen(
    expenseViewModel : ExpenseViewModel = koinViewModel()
) {

    //val expenses by expenseViewModel.expenses.collectAsState()
    val totalSpent by expenseViewModel.totalSpentUiState.collectAsState()

    HomeScreenContent(
        //expenses = expenses,
        totalSpent = totalSpent,
        onAddExpense = { expenseViewModel.addExpense(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    //homeDetails: HomeDetails,
    totalSpent: TotalSpentUiState,
    budgetOverViews: List<BudgetCategory> = emptyList(),
    onAddExpense: (Expense) -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
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
                    IconButton(onClick = {
                        // Got to notifications
                    }) { Icon(Icons.Default.Notifications, contentDescription = "Notifications") }
                    IconButton(onClick = {
                        // Got to settings
                    }) { Icon(Icons.Default.Settings, contentDescription = "Settings") }
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
            TotalSpentCard(totalSpent = totalSpent, 0.0)

            BudgetOverviewCard(expenses = budgetOverViews)

            //RecentTransactionsview(expenses = expenses)
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

fun getCategoryColor(category: ExpenseCategory): Color {
    return when (category) {
        ExpenseCategory.FOOD -> CategoryFood
        ExpenseCategory.TRANSPORT -> CategoryTransport
        ExpenseCategory.SHOPPING -> CategoryShopping
        ExpenseCategory.BILLS -> CategoryBills
        ExpenseCategory.ENTERTAINMENT -> CategoryEntertainment
        ExpenseCategory.OTHER -> CategoryOther
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
    val sampleExpenses = listOf(
        Expense(1,
            "Groceries",
            50.0,

            ExpenseCategory.FOOD.name,
            yesterday
        ),
        Expense(2,
            "Bus Fare",
            2.5,
            ExpenseCategory.TRANSPORT.name,
            yesterday
        ),
        Expense(3,
            "Movie",
            15.0,
            ExpenseCategory.ENTERTAINMENT.name,
            yesterday
        ),
        Expense(4,
            "Rent",
            1200.0,
            ExpenseCategory.BILLS.name,
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
           // expenses = sampleExpenses,
            totalSpent = TotalSpentUiState.Success("1250.00"),
            budgetOverViews = sampleBudgetCategory
        )
    }
}
