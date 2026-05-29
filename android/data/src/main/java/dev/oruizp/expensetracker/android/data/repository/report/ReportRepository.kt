package dev.oruizp.expensetracker.android.data.repository.report

import dev.oruizp.expensetracker.data.graphql.type.Dashboard
import dev.oruizp.expensetracker.data.graphql.type.MonthlyReport
import dev.oruizp.expensetracker.data.graphql.type.Period
import dev.oruizp.expensetracker.data.graphql.type.ReportSummary
import dev.oruizp.expensetracker.data.graphql.type.YearlyReport

interface ReportRepository {
    suspend fun getDashboard(): Dashboard
    suspend fun getReportSummary(period: Period, month: Int, year: Int): ReportSummary
    suspend fun getReportMonthly(month: Int, year: Int): MonthlyReport
    suspend fun getReportYearly(year: Int): YearlyReport
}