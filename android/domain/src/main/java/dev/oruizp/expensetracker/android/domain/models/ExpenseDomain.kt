package dev.oruizp.expensetracker.android.domain.models

enum class ExpenseCategoryDomain {
    FOOD,
    TRANSPORT,
    SHOPPING,
    BILLS,
    ENTERTAINMENT,
    OTHER
}

data class ExpenseDomain(
    val id: Long,
    val title: String,
    val amount: Double,
    val category: ExpenseCategoryDomain,
    val timestamp: Long,
)
