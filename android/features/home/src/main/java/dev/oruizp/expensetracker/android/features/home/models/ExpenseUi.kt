package dev.oruizp.expensetracker.android.features.home.models

enum class ExpenseCategoryUi {
    FOOD,
    TRANSPORT,
    SHOPPING,
    BILLS,
    ENTERTAINMENT,
    OTHER
}

data class ExpenseUi(
    val id: Long,
    val title: String,
    val amount: Double,
    val category: ExpenseCategoryUi,
    val timestamp: Long,
)
