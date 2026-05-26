package dev.oruizp.expensetracker.backend.repositories

import dev.oruizp.expensetracker.backend.database.ExpensesTable
import dev.oruizp.expensetracker.backend.database.dbQuery
import dev.oruizp.expensetracker.backend.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SortOrder as ExposedSortOrder
import java.time.LocalDate
import java.time.LocalDateTime

class ExpenseRepositoryImpl : ExpenseRepository {

    override suspend fun create(input: CreateExpenseInput, userId: Int): Expense = dbQuery {
        val now = LocalDateTime.now()
        ExpensesTable.insert {
            it[ExpensesTable.userId] = userId
            it[ExpensesTable.title] = input.title
            it[ExpensesTable.amount] = input.amount
            it[ExpensesTable.categoryId] = input.categoryId.toInt()
            it[ExpensesTable.expenseDate] = LocalDate.parse(input.date)
            it[ExpensesTable.description] = input.description
            it[ExpensesTable.clientId] = input.clientId
            it[ExpensesTable.createdAt] = now
            it[ExpensesTable.updatedAt] = now
        }.resultedValues?.single()?.toExpense() ?: throw RuntimeException("Failed to create expense")
    }

    override suspend fun findById(id: Int): Expense? = dbQuery {
        ExpensesTable
            .selectAll()
            .where { ExpensesTable.id eq id and (ExpensesTable.isDeleted eq false) }
            .singleOrNull()
            ?.toExpense()
    }

    override suspend fun findByFilter(
        filter: ExpenseFilter?,
        pagination: PaginationInput?,
        sort: SortInput?,
    ): PaginatedResult = dbQuery {
        val query = buildQuery(filter, sort)
        val totalCount = query.count()
        val totalAmount = query.map { it[ExpensesTable.amount] }.sum()
        val limit = pagination?.first ?: 20
        val items = query.limit(limit).map { it.toExpense() }
        PaginatedResult(items, totalCount.toInt(), totalAmount)
    }

    override suspend fun update(id: Int, input: UpdateExpenseInput): Expense? = dbQuery {
        val updated = ExpensesTable.update({
            ExpensesTable.id eq id and (ExpensesTable.isDeleted eq false)
        }) {
            input.title?.let { v -> it[ExpensesTable.title] = v }
            input.amount?.let { v -> it[ExpensesTable.amount] = v }
            input.categoryId?.let { v -> it[ExpensesTable.categoryId] = v.toInt() }
            input.date?.let { v -> it[ExpensesTable.expenseDate] = LocalDate.parse(v) }
            input.description?.let { v -> it[ExpensesTable.description] = v }
        }
        if (updated > 0) findById(id) else null
    }

    override suspend fun delete(id: Int): Boolean = dbQuery {
        ExpensesTable.update({ ExpensesTable.id eq id }) {
            it[ExpensesTable.isDeleted] = true
        } > 0
    }

    override suspend fun findByClientId(clientId: String): Expense? = dbQuery {
        ExpensesTable
            .selectAll()
            .where { ExpensesTable.clientId eq clientId }
            .singleOrNull()
            ?.toExpense()
    }

    private fun buildQuery(filter: ExpenseFilter?, sort: SortInput?): Query {
        var query: Query = ExpensesTable
            .selectAll()
            .where { ExpensesTable.isDeleted eq false }

        if (filter != null) {
            if (filter.search != null) {
                query = query.andWhere { ExpensesTable.title like "%${filter.search}%" }
            }
            if (filter.categoryId != null) {
                query = query.andWhere { ExpensesTable.categoryId eq filter.categoryId.toInt() }
            }
            if (filter.dateFrom != null) {
                query = query.andWhere { ExpensesTable.expenseDate greaterEq LocalDate.parse(filter.dateFrom) }
            }
            if (filter.dateTo != null) {
                query = query.andWhere { ExpensesTable.expenseDate lessEq LocalDate.parse(filter.dateTo) }
            }
            if (filter.minAmount != null) {
                query = query.andWhere { ExpensesTable.amount greaterEq filter.minAmount }
            }
            if (filter.maxAmount != null) {
                query = query.andWhere { ExpensesTable.amount lessEq filter.maxAmount }
            }
        }

        if (sort != null) {
            val col = when (sort.field) {
                SortField.DATE -> ExpensesTable.expenseDate
                SortField.AMOUNT -> ExpensesTable.amount
                SortField.TITLE -> ExpensesTable.title
            }
            query = if (sort.order == SortOrder.DESC) {
                query.orderBy(col, ExposedSortOrder.DESC)
            } else {
                query.orderBy(col, ExposedSortOrder.ASC)
            }
        } else {
            query = query.orderBy(ExpensesTable.expenseDate, ExposedSortOrder.DESC)
        }

        return query
    }
}

internal fun ResultRow.toExpense(): Expense {
    val row = this
    val expenseId: Int = row[ExpensesTable.id]
    val expenseTitle: String = row[ExpensesTable.title]
    val expenseAmount: Double = row[ExpensesTable.amount]
    val expenseCategoryId: Int = row[ExpensesTable.categoryId]
    val expenseDate: java.time.LocalDate = row[ExpensesTable.expenseDate]
    val expenseDescription: String? = row[ExpensesTable.description]
    val expenseReceiptUrl: String? = row[ExpensesTable.receiptUrl]
    val expenseLocationLat: Double? = row[ExpensesTable.locationLat]
    val expenseLocationLng: Double? = row[ExpensesTable.locationLng]
    val expenseClientId: String? = row[ExpensesTable.clientId]
    val expenseConflictVersion: Int = row[ExpensesTable.conflictVersion]
    val expenseCreatedAt: java.time.LocalDateTime = row[ExpensesTable.createdAt]
    val expenseUpdatedAt: java.time.LocalDateTime = row[ExpensesTable.updatedAt]
    return Expense(
        id = expenseId,
        title = expenseTitle,
        amount = expenseAmount,
        categoryId = expenseCategoryId,
        category = null,
        date = expenseDate.toString(),
        description = expenseDescription,
        receiptUrl = expenseReceiptUrl,
        locationLat = expenseLocationLat,
        locationLng = expenseLocationLng,
        clientId = expenseClientId,
        conflictVersion = expenseConflictVersion,
        createdAt = expenseCreatedAt.toString(),
        updatedAt = expenseUpdatedAt.toString(),
    )
}
