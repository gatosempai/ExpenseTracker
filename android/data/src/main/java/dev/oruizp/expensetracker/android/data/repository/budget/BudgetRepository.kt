package dev.oruizp.expensetracker.android.data.repository.budget

import dev.oruizp.expensetracker.data.graphql.type.Budget

interface BudgetRepository {
    suspend fun getBudgets(): List<Budget>
    suspend fun getBudget(id: String): Budget?
    suspend fun createBudget(budget: Budget): Budget
    suspend fun updateBudget(id: String, budget: Budget): Budget
    suspend fun deleteBudget(id: String): Boolean
}