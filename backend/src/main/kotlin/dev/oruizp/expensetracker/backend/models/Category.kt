package dev.oruizp.expensetracker.backend.models

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Int = 0,
    val name: String,
    val color: String,
    val icon: String,
    val isDefault: Boolean = false,
    val expenseCount: Int = 0,
    val totalAmount: Double = 0.0,
)

@Serializable
data class CreateCategoryInput(
    val name: String,
    val color: String,
    val icon: String? = null,
)

@Serializable
data class UpdateCategoryInput(
    val name: String? = null,
    val color: String? = null,
    val icon: String? = null,
)
