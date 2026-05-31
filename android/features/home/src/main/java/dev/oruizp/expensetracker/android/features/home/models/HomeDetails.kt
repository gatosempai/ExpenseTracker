package dev.oruizp.expensetracker.android.features.home.models

import dev.oruizp.expensetracker.android.features.home.state.ExpensesUiState
import dev.oruizp.expensetracker.android.features.home.state.TotalSpentUiState

data class HomeDetails(
    val totalSpent: TotalSpentUiState,
    val expenses: ExpensesUiState,
    val percentageOffset: Double
)
