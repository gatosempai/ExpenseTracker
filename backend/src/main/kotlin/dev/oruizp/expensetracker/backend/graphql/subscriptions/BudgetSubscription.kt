package dev.oruizp.expensetracker.backend.graphql.subscriptions

import com.expediagroup.graphql.server.operations.Subscription
import dev.oruizp.expensetracker.backend.models.*
import dev.oruizp.expensetracker.backend.services.BudgetService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BudgetSubscription(private val budgetService: BudgetService) : Subscription {

    fun budgetAlert(threshold: Float? = null): Flow<BudgetAlert> = flow {
        while (true) {
            val budgets = budgetService.findAll()
            for (budget in budgets) {
                val effectiveThreshold = threshold ?: 90f
                if (budget.isOverLimit) {
                    emit(
                        BudgetAlert(
                            budget = budget,
                            message = "Budget over limit: ${budget.category?.name ?: "Unknown"} exceeded ${budget.limit}",
                            severity = AlertSeverity.CRITICAL,
                        )
                    )
                } else if (budget.isWarning && budget.percentage >= effectiveThreshold) {
                    emit(
                        BudgetAlert(
                            budget = budget,
                            message = "Budget warning: ${budget.category?.name ?: "Unknown"} at ${String.format("%.0f", budget.percentage)}%",
                            severity = AlertSeverity.WARNING,
                        )
                    )
                }
            }
            delay(30_000)
        }
    }

    fun expenseUpdated(): Flow<Expense> = flow {
        while (true) {
            delay(30_000)
        }
    }
}
