package dev.oruizp.expensetracker.backend.models

import kotlinx.serialization.Serializable

@Serializable
data class Expense(
    val id: Int = 0,
    val title: String,
    val amount: Double,
    val category: Category? = null,
    val categoryId: Int = 0,
    val date: String,
    val description: String? = null,
    val receiptUrl: String? = null,
    val locationLat: Double? = null,
    val locationLng: Double? = null,
    val clientId: String? = null,
    val conflictVersion: Int = 1,
    val createdAt: String = "",
    val updatedAt: String = "",
)

@Serializable
data class ExpenseConnection(
    val edges: List<ExpenseEdge>,
    val pageInfo: PageInfo,
    val totalCount: Int,
    val totalAmount: Double,
)

@Serializable
data class ExpenseEdge(
    val node: Expense,
    val cursor: String,
)

@Serializable
data class PageInfo(
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean,
    val startCursor: String? = null,
    val endCursor: String? = null,
)

@Serializable
data class ExpenseFilter(
    val search: String? = null,
    val categoryId: String? = null,
    val dateFrom: String? = null,
    val dateTo: String? = null,
    val minAmount: Double? = null,
    val maxAmount: Double? = null,
)

@Serializable
data class PaginationInput(
    val first: Int = 20,
    val after: String? = null,
    val last: Int? = null,
    val before: String? = null,
)

@Serializable
data class SortInput(
    val field: SortField,
    val order: SortOrder,
)

enum class SortField { DATE, AMOUNT, TITLE }

enum class SortOrder { ASC, DESC }

@Serializable
data class CreateExpenseInput(
    val title: String,
    val amount: Double,
    val categoryId: String,
    val date: String,
    val description: String? = null,
    val clientId: String? = null,
)

@Serializable
data class UpdateExpenseInput(
    val title: String? = null,
    val amount: Double? = null,
    val categoryId: String? = null,
    val date: String? = null,
    val description: String? = null,
)

@Serializable
data class DeleteResult(
    val success: Boolean,
    val id: String,
)
