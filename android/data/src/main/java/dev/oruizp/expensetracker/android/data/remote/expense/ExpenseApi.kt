package dev.oruizp.expensetracker.android.data.remote.expense

import dev.oruizp.expensetracker.data.graphql.type.CreateExpenseInput
import dev.oruizp.expensetracker.data.graphql.type.UpdateExpenseInput

interface ExpenseApi {
    suspend fun getExpenses(): List<ExpenseDto>
    suspend fun getExpense(id: String): ExpenseDto?
    suspend fun createExpense(input: CreateExpenseInput): ExpenseDto
    suspend fun updateExpense(id: String, input: UpdateExpenseInput): ExpenseDto
    suspend fun deleteExpense(id: String): Boolean
}
