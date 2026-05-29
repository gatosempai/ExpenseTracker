package dev.oruizp.expensetracker.android.domain.models

data class Expense(
    val id: Long,
    val title: String,
    val amount: Double,
    val category: String,
    val timestamp: Long,
)
