package dev.oruizp.expensetracker.android.domain.models

data class HomeDetails(
    val totalSpent: Double,
    val expenses: List<Expense>,
    val percentageOffset: Double
) {
}