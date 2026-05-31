package dev.oruizp.expensetracker.android.domain.repository

import dev.oruizp.expensetracker.android.domain.models.ExpenseDomain
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getAllExpenses(): Flow<List<ExpenseDomain>>
    suspend fun addExpense(expense: ExpenseDomain)
    suspend fun deleteExpense(expense: ExpenseDomain)
    fun getTotalSpent(): Flow<Double>

}