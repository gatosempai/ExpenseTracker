package dev.oruizp.expensetracker.backend.repositories

import dev.oruizp.expensetracker.backend.models.*

interface BudgetRepository {
    suspend fun findAll(period: Period? = null): List<Budget>
    suspend fun findById(id: Int): Budget?
    suspend fun create(input: CreateBudgetInput, userId: Int = 1): Budget
    suspend fun update(id: Int, input: UpdateBudgetInput): Budget?
    suspend fun delete(id: Int): Boolean
}
