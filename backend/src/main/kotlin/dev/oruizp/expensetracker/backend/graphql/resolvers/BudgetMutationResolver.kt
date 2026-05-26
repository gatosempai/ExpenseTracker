package dev.oruizp.expensetracker.backend.graphql.resolvers

import com.expediagroup.graphql.server.operations.Mutation
import dev.oruizp.expensetracker.backend.models.*
import dev.oruizp.expensetracker.backend.services.BudgetService

class BudgetMutationResolver(private val budgetService: BudgetService) : Mutation {

    suspend fun createBudget(input: CreateBudgetInput): Budget {
        return budgetService.create(input)
    }

    suspend fun updateBudget(id: String, input: UpdateBudgetInput): Budget? {
        return budgetService.update(id.toInt(), input)
    }

    suspend fun deleteBudget(id: String): DeleteResult {
        val success = budgetService.delete(id.toInt())
        return DeleteResult(success = success, id = id)
    }
}
