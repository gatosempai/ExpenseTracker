package dev.oruizp.expensetracker.backend.repositories

import dev.oruizp.expensetracker.backend.database.BudgetsTable
import dev.oruizp.expensetracker.backend.database.dbQuery
import dev.oruizp.expensetracker.backend.models.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.*
import java.time.LocalDate

class BudgetRepositoryImpl : BudgetRepository {

    override suspend fun findAll(period: Period?): List<Budget> = dbQuery {
        val query = if (period != null) {
            BudgetsTable.selectAll().where { BudgetsTable.period eq period.name }
        } else {
            BudgetsTable.selectAll()
        }
        query.map { it.toBudget() }
    }

    override suspend fun findById(id: Int): Budget? = dbQuery {
        BudgetsTable
            .selectAll()
            .where { BudgetsTable.id eq id }
            .singleOrNull()
            ?.toBudget()
    }

    override suspend fun create(input: CreateBudgetInput, userId: Int): Budget = dbQuery {
        BudgetsTable.insert {
            it[BudgetsTable.userId] = userId
            it[BudgetsTable.categoryId] = input.categoryId.toInt()
            it[BudgetsTable.limit] = input.limit
            it[BudgetsTable.period] = input.period.name
            it[BudgetsTable.startDate] = LocalDate.now()
        }.resultedValues?.single()?.toBudget() ?: throw RuntimeException("Failed to create budget")
    }

    override suspend fun update(id: Int, input: UpdateBudgetInput): Budget? = dbQuery {
        val updated = BudgetsTable.update({ BudgetsTable.id eq id }) {
            input.limit?.let { v -> it[BudgetsTable.limit] = v }
            input.period?.let { v -> it[BudgetsTable.period] = v.name }
        }
        if (updated > 0) findById(id) else null
    }

    override suspend fun delete(id: Int): Boolean = dbQuery {
        BudgetsTable.deleteWhere { BudgetsTable.id eq id } > 0
    }
}

internal fun ResultRow.toBudget(): Budget {
    val budgetId: Int = this[BudgetsTable.id]
    val budgetCategoryId: Int = this[BudgetsTable.categoryId]
    val budgetLimit: Double = this[BudgetsTable.limit]
    val budgetPeriod: String = this[BudgetsTable.period]
    val budgetStartDate: java.time.LocalDate = this[BudgetsTable.startDate]
    return Budget(
        id = budgetId,
        categoryId = budgetCategoryId,
        limit = budgetLimit,
        period = Period.valueOf(budgetPeriod),
        startDate = budgetStartDate.toString(),
    )
}
