package dev.oruizp.expensetracker.backend.services

import dev.oruizp.expensetracker.backend.models.*
import dev.oruizp.expensetracker.backend.repositories.BudgetRepository
import dev.oruizp.expensetracker.backend.repositories.ExpenseRepository
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class BudgetService(
    private val budgetRepository: BudgetRepository,
    private val expenseRepository: ExpenseRepository,
) {

    suspend fun findAll(period: Period? = null): List<Budget> =
        budgetRepository.findAll(period).map { it.withComputedFields() }

    suspend fun findById(id: Int): Budget? =
        budgetRepository.findById(id)?.withComputedFields()

    suspend fun create(input: CreateBudgetInput): Budget {
        if (input.limit <= 0) throw IllegalArgumentException("Budget limit must be greater than 0")
        return budgetRepository.create(input).withComputedFields()
    }

    suspend fun update(id: Int, input: UpdateBudgetInput): Budget? {
        input.limit?.let { if (it <= 0) throw IllegalArgumentException("Budget limit must be greater than 0") }
        return budgetRepository.update(id, input)?.withComputedFields()
    }

    suspend fun delete(id: Int): Boolean = budgetRepository.delete(id)

    private suspend fun Budget.withComputedFields(): Budget {
        val now = LocalDate.now()
        val endDate = when (period) {
            Period.WEEKLY -> LocalDate.parse(startDate).plusWeeks(1)
            Period.MONTHLY -> LocalDate.parse(startDate).plusMonths(1)
            Period.YEARLY -> LocalDate.parse(startDate).plusYears(1)
        }
        val daysLeft = ChronoUnit.DAYS.between(now, endDate).toInt().coerceAtLeast(0)
        val daysInPeriod = ChronoUnit.DAYS.between(LocalDate.parse(startDate), endDate)

        val spent = expenseRepository.findByFilter(
            ExpenseFilter(
                categoryId = categoryId.toString(),
                dateFrom = startDate,
                dateTo = endDate.toString(),
            ),
            null, null,
        ).totalAmount

        val percentage = if (limit > 0) (spent / limit * 100).coerceAtMost(100.0) else 0.0
        return copy(
            spent = spent,
            percentage = percentage,
            daysRemaining = daysLeft,
            isOverLimit = percentage >= 100,
            isWarning = percentage >= 90,
        )
    }
}
