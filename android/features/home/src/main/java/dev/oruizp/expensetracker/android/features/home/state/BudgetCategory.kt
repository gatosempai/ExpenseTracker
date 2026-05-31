package dev.oruizp.expensetracker.android.features.home.state

import androidx.compose.ui.graphics.Color

data class BudgetCategory(
    val name: String,
    val spent: Float,    // e.g. 45.0
    val limit: Float,    // e.g. 100.0
    val color: Color
)