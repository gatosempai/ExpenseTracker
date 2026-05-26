package dev.oruizp.expensetracker.backend.services

import dev.oruizp.expensetracker.backend.models.*
import dev.oruizp.expensetracker.backend.repositories.ExpenseRepository
import java.time.LocalDate

class ReportService(
    private val expenseRepository: ExpenseRepository,
    private val categoryService: CategoryService,
) {

    suspend fun summary(period: Period?, year: Int?, month: Int?): ReportSummary {
        val now = LocalDate.now()
        val currentYear = year ?: now.year
        val currentMonth = month ?: now.monthValue

        val (startDate, endDate) = when (period) {
            Period.WEEKLY -> {
                val start = now.minusDays(now.dayOfWeek.value.toLong() - 1)
                start to start.plusDays(6)
            }
            Period.MONTHLY -> {
                val start = LocalDate.of(currentYear, currentMonth, 1)
                start to start.withDayOfMonth(start.lengthOfMonth())
            }
            Period.YEARLY -> {
                val start = LocalDate.of(currentYear, 1, 1)
                start to LocalDate.of(currentYear, 12, 31)
            }
            null -> {
                val start = LocalDate.of(currentYear, currentMonth, 1)
                start to start.withDayOfMonth(start.lengthOfMonth())
            }
        }

        val result = expenseRepository.findByFilter(
            ExpenseFilter(dateFrom = startDate.toString(), dateTo = endDate.toString()),
            null, null,
        )

        val categories = categoryService.findAll()
        val categoryBreakdown = categories.map { cat ->
            val catTotal = result.items.filter { it.categoryId == cat.id }.sumOf { it.amount }
            CategoryBreakdown(
                category = cat,
                amount = catTotal,
                percentage = if (result.totalAmount > 0) (catTotal / result.totalAmount * 100) else 0.0,
                transactionCount = result.items.count { it.categoryId == cat.id },
            )
        }

        val dailyTrend = startDate.datesUntil(endDate.plusDays(1)).toList().map { date ->
            val dayTotal = result.items.filter { it.date == date.toString() }.sumOf { it.amount }
            DailyDataPoint(
                date = date.toString(),
                amount = dayTotal,
                transactionCount = result.items.count { it.date == date.toString() },
            )
        }

        return ReportSummary(
            totalSpent = result.totalAmount,
            averageDaily = if (dailyTrend.isNotEmpty()) result.totalAmount / dailyTrend.size else 0.0,
            categoryBreakdown = categoryBreakdown.filter { it.amount > 0 },
            dailyTrend = dailyTrend,
            topExpenses = result.items.sortedByDescending { it.amount }.take(5),
            previousPeriodComparison = Comparison(0.0, 0.0, TrendDirection.SAME),
        )
    }

    suspend fun monthly(year: Int, month: Int): MonthlyReport {
        val summary = summary(Period.MONTHLY, year, month)
        return MonthlyReport(
            year = year,
            month = month,
            totalSpent = summary.totalSpent,
            days = summary.dailyTrend,
            categoryBreakdown = summary.categoryBreakdown,
            topExpenses = summary.topExpenses,
        )
    }

    suspend fun yearly(year: Int): YearlyReport {
        var totalSpent = 0.0
        val monthlyBreakdown = (1..12).map { m ->
            val result = expenseRepository.findByFilter(
                ExpenseFilter(
                    dateFrom = LocalDate.of(year, m, 1).toString(),
                    dateTo = LocalDate.of(year, m, 1).withDayOfMonth(
                        LocalDate.of(year, m, 1).lengthOfMonth()
                    ).toString(),
                ),
                null, null,
            )
            totalSpent += result.totalAmount
            MonthlySummary(m, result.totalAmount, result.items.size)
        }

        val categories = categoryService.findAll()
        val yearlyResult = expenseRepository.findByFilter(
            ExpenseFilter(
                dateFrom = LocalDate.of(year, 1, 1).toString(),
                dateTo = LocalDate.of(year, 12, 31).toString(),
            ),
            null, null,
        )
        val categoryBreakdown = categories.map { cat ->
            val catTotal = yearlyResult.items.filter { it.categoryId == cat.id }.sumOf { it.amount }
            CategoryBreakdown(
                category = cat,
                amount = catTotal,
                percentage = if (totalSpent > 0) (catTotal / totalSpent * 100) else 0.0,
                transactionCount = yearlyResult.items.count { it.categoryId == cat.id },
            )
        }

        return YearlyReport(
            year = year,
            totalSpent = totalSpent,
            monthlyBreakdown = monthlyBreakdown,
            categoryBreakdown = categoryBreakdown.filter { it.amount > 0 },
            comparison = Comparison(0.0, 0.0, TrendDirection.SAME),
        )
    }
}
