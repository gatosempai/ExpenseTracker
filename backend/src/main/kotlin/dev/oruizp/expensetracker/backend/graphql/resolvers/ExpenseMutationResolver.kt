package dev.oruizp.expensetracker.backend.graphql.resolvers

import com.expediagroup.graphql.server.operations.Mutation
import dev.oruizp.expensetracker.backend.models.*
import dev.oruizp.expensetracker.backend.services.ExpenseService

class ExpenseMutationResolver(private val expenseService: ExpenseService) : Mutation {

    suspend fun createExpense(input: CreateExpenseInput): Expense {
        return expenseService.create(input)
    }

    suspend fun updateExpense(id: String, input: UpdateExpenseInput): Expense? {
        return expenseService.update(id.toInt(), input)
    }

    suspend fun deleteExpense(id: String): DeleteResult {
        val success = expenseService.delete(id.toInt())
        return DeleteResult(success = success, id = id)
    }
}
