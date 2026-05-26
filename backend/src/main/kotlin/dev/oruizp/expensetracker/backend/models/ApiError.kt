package dev.oruizp.expensetracker.backend.models

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(
    val code: String,
    val message: String,
)

@Serializable
data class ValidationError(
    val code: String = "VALIDATION_ERROR",
    val message: String,
    val field: String,
)

@Serializable
data class NotFoundError(
    val code: String = "NOT_FOUND",
    val message: String,
    val resourceType: String,
    val resourceId: String,
)

@Serializable
data class BudgetAlert(
    val budget: Budget,
    val message: String,
    val severity: AlertSeverity,
)

enum class AlertSeverity { WARNING, CRITICAL }
