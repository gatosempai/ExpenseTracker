package dev.oruizp.expensetracker.android.data.remote.expense

data class ExpenseDto(
    val id: Int,
    val title: String,
    val description: String?,
    val amount: Double,
    val date: String,
    val categoryId: Int,
    val categoryName: String?,
    val clientId: String?,
    val conflictVersion: Int,
    val createdAt: String,
    val updatedAt: String,
)
