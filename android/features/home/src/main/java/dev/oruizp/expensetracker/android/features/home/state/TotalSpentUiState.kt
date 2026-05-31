package dev.oruizp.expensetracker.android.features.home.state

sealed class TotalSpentUiState {
    object Loading: TotalSpentUiState()
    data class Success(val totalSpent: String) : TotalSpentUiState()
    data class Error(val message: String) : TotalSpentUiState()
}