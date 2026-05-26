package dev.oruizp.expensetracker.backend.models

import kotlinx.serialization.Serializable

@Serializable
data class Budget(
    val id: Int = 0,
    val category: Category? = null,
    val categoryId: Int = 0,
    val limit: Double,
    val period: Period,
    val spent: Double = 0.0,
    val percentage: Double = 0.0,
    val daysRemaining: Int = 0,
    val isOverLimit: Boolean = false,
    val isWarning: Boolean = false,
    val startDate: String = "",
)

enum class Period { WEEKLY, MONTHLY, YEARLY }

@Serializable
data class CreateBudgetInput(
    val categoryId: String,
    val limit: Double,
    val period: Period,
)

@Serializable
data class UpdateBudgetInput(
    val limit: Double? = null,
    val period: Period? = null,
)
