package dev.oruizp.expensetracker.android.domain.repository

import dev.oruizp.expensetracker.android.domain.models.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getAllExpenses(): Flow<List<Expense>>
    suspend fun addExpense(expense: Expense)
    suspend fun deleteExpense(expense: Expense)
    fun getTotalSpent(): Flow<Double>

}