package dev.oruizp.expensetracker.backend.graphql.resolvers

import com.expediagroup.graphql.server.operations.Query
import dev.oruizp.expensetracker.backend.models.*
import dev.oruizp.expensetracker.backend.services.BudgetService
import dev.oruizp.expensetracker.backend.services.ReportService

class DashboardResolver(
    private val reportService: ReportService,
    private val budgetService: BudgetService,
) : Query {

    suspend fun dashboard(): Dashboard {
        val summary = reportService.summary(null, null, null)
        val budgets = budgetService.findAll()
        return Dashboard(
            totalSpent = summary.totalSpent,
            previousPeriodComparison = summary.previousPeriodComparison,
            recentTransactions = summary.topExpenses.take(5),
            budgetOverview = budgets,
        )
    }
}
