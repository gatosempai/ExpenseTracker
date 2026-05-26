package dev.oruizp.expensetracker.backend.graphql.resolvers

import com.expediagroup.graphql.server.operations.Query
import dev.oruizp.expensetracker.backend.models.*
import dev.oruizp.expensetracker.backend.services.BudgetService

class BudgetResolver(private val budgetService: BudgetService) : Query {

    suspend fun budgets(period: Period? = null): List<Budget> {
        return budgetService.findAll(period)
    }

    suspend fun budget(id: String): Budget? {
        return budgetService.findById(id.toInt())
    }
}
