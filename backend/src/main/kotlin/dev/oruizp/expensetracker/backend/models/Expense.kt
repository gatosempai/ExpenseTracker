package dev.oruizp.expensetracker.backend.models

import kotlinx.serialization.Serializable

@Serializable
data class Expense(
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String,
    val date: String,
)
