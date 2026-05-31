package dev.oruizp.expensetracker.android.features.home.state

import dev.oruizp.expensetracker.android.features.home.models.ExpenseUi

sealed class ExpensesUiState {
    object Loading : ExpensesUiState()
    data class Success(val expens: List<ExpenseUi>) : ExpensesUiState()
    data class Error(val message: String) : ExpensesUiState()
}