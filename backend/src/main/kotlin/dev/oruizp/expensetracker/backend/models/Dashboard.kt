package dev.oruizp.expensetracker.backend.models

import kotlinx.serialization.Serializable

@Serializable
data class Dashboard(
    val totalSpent: Double,
    val previousPeriodComparison: Comparison,
    val recentTransactions: List<Expense>,
    val budgetOverview: List<Budget>,
)
