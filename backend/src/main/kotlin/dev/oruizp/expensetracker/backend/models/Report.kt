package dev.oruizp.expensetracker.backend.models

import kotlinx.serialization.Serializable

@Serializable
data class ReportSummary(
    val totalSpent: Double,
    val totalIncome: Double = 0.0,
    val netAmount: Double = 0.0,
    val averageDaily: Double,
    val categoryBreakdown: List<CategoryBreakdown>,
    val dailyTrend: List<DailyDataPoint>,
    val topExpenses: List<Expense>,
    val previousPeriodComparison: Comparison,
)

@Serializable
data class CategoryBreakdown(
    val category: Category,
    val amount: Double,
    val percentage: Double,
    val transactionCount: Int,
)

@Serializable
data class DailyDataPoint(
    val date: String,
    val amount: Double,
    val transactionCount: Int,
)

@Serializable
data class MonthlyReport(
    val year: Int,
    val month: Int,
    val totalSpent: Double,
    val days: List<DailyDataPoint>,
    val categoryBreakdown: List<CategoryBreakdown>,
    val topExpenses: List<Expense>,
)

@Serializable
data class YearlyReport(
    val year: Int,
    val totalSpent: Double,
    val monthlyBreakdown: List<MonthlySummary>,
    val categoryBreakdown: List<CategoryBreakdown>,
    val comparison: Comparison,
)

@Serializable
data class MonthlySummary(
    val month: Int,
    val totalSpent: Double,
    val transactionCount: Int,
)

@Serializable
data class Comparison(
    val amount: Double,
    val percentage: Double,
    val trend: TrendDirection,
)

enum class TrendDirection { UP, DOWN, SAME }
