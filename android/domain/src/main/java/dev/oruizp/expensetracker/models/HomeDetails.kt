package dev.oruizp.expensetracker.models

data class HomeDetails(
    val totalSpent: Double,
    val expenses: List<Expense>,
    val percentageOffset: Double
) {
}