package dev.oruizp.expensetracker.android.data.remote.expense

import com.apollographql.apollo.ApolloClient
import dev.oruizp.expensetracker.data.graphql.CreateExpenseMutation
import dev.oruizp.expensetracker.data.graphql.DeleteExpenseMutation
import dev.oruizp.expensetracker.data.graphql.ExpenseQuery
import dev.oruizp.expensetracker.data.graphql.ExpensesQuery
import dev.oruizp.expensetracker.data.graphql.UpdateExpenseMutation
import dev.oruizp.expensetracker.data.graphql.type.CreateExpenseInput
import dev.oruizp.expensetracker.data.graphql.type.UpdateExpenseInput

class ExpenseApiGraphQL(
    private val apolloClient: ApolloClient
) : ExpenseApi {

    override suspend fun getExpenses(): List<ExpenseDto> {
        val response = apolloClient.query(ExpensesQuery()).execute()
        val data = response.dataOrThrow()
        return data.expenses.edges.map { edge ->
            val node = edge.node
            ExpenseDto(
                id = node.id,
                title = node.title,
                description = node.description,
                amount = node.amount,
                date = node.date,
                categoryId = node.categoryId,
                categoryName = node.category?.name,
                clientId = node.clientId,
                conflictVersion = node.conflictVersion,
                createdAt = node.createdAt,
                updatedAt = node.updatedAt,
            )
        }
    }

    override suspend fun getExpense(id: String): ExpenseDto? {
        val response = apolloClient.query(ExpenseQuery(id = id)).execute()
        val expense = response.dataOrThrow().expense ?: return null
        return ExpenseDto(
            id = expense.id,
            title = expense.title,
            description = expense.description,
            amount = expense.amount,
            date = expense.date,
            categoryId = expense.categoryId,
            categoryName = expense.category?.name,
            clientId = expense.clientId,
            conflictVersion = expense.conflictVersion,
            createdAt = expense.createdAt,
            updatedAt = expense.updatedAt,
        )
    }

    override suspend fun createExpense(input: CreateExpenseInput): ExpenseDto {
        val response = apolloClient.mutation(CreateExpenseMutation(input = input)).execute()
        val created = response.dataOrThrow().createExpense
        return ExpenseDto(
            id = created.id,
            title = created.title,
            description = created.description,
            amount = created.amount,
            date = created.date,
            categoryId = created.categoryId,
            categoryName = null,
            clientId = created.clientId,
            conflictVersion = created.conflictVersion,
            createdAt = created.createdAt,
            updatedAt = created.updatedAt,
        )
    }

    override suspend fun updateExpense(id: String, input: UpdateExpenseInput): ExpenseDto {
        val response = apolloClient.mutation(UpdateExpenseMutation(id = id, input = input)).execute()
        val updated = response.dataOrThrow().updateExpense ?: error("Expense not found")
        return ExpenseDto(
            id = updated.id,
            title = updated.title,
            description = updated.description,
            amount = updated.amount,
            date = updated.date,
            categoryId = updated.categoryId,
            categoryName = null,
            clientId = updated.clientId,
            conflictVersion = updated.conflictVersion,
            createdAt = updated.createdAt,
            updatedAt = updated.updatedAt,
        )
    }

    override suspend fun deleteExpense(id: String): Boolean {
        val response = apolloClient.mutation(DeleteExpenseMutation(id = id)).execute()
        return response.dataOrThrow().deleteExpense.success
    }
}
