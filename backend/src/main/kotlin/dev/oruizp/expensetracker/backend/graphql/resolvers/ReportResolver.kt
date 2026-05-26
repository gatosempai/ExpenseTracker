package dev.oruizp.expensetracker.backend.graphql.resolvers

import com.expediagroup.graphql.server.operations.Query
import dev.oruizp.expensetracker.backend.models.*
import dev.oruizp.expensetracker.backend.services.ReportService

class ReportResolver(private val reportService: ReportService) : Query {

    suspend fun reportSummary(period: Period? = null, year: Int? = null, month: Int? = null): ReportSummary {
        return reportService.summary(period, year, month)
    }

    suspend fun reportMonthly(year: Int, month: Int): MonthlyReport {
        return reportService.monthly(year, month)
    }

    suspend fun reportYearly(year: Int): YearlyReport {
        return reportService.yearly(year)
    }
}
