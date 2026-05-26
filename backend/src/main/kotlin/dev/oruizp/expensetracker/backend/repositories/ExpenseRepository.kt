package dev.oruizp.expensetracker.backend.repositories

import dev.oruizp.expensetracker.backend.models.*

interface ExpenseRepository {
    suspend fun create(input: CreateExpenseInput, userId: Int = 1): Expense
    suspend fun findById(id: Int): Expense?
    suspend fun findByFilter(
        filter: ExpenseFilter?,
        pagination: PaginationInput?,
        sort: SortInput?,
    ): PaginatedResult
    suspend fun update(id: Int, input: UpdateExpenseInput): Expense?
    suspend fun delete(id: Int): Boolean
    suspend fun findByClientId(clientId: String): Expense?
}

data class PaginatedResult(
    val items: List<Expense>,
    val totalCount: Int,
    val totalAmount: Double,
)
