package dev.oruizp.expensetracker.backend.graphql.resolvers

import com.expediagroup.graphql.server.operations.Query
import dev.oruizp.expensetracker.backend.models.*
import dev.oruizp.expensetracker.backend.repositories.PaginatedResult
import dev.oruizp.expensetracker.backend.services.ExpenseService
import java.util.Base64

class ExpenseResolver(private val expenseService: ExpenseService) : Query {

    suspend fun expenses(
        filter: ExpenseFilter? = null,
        pagination: PaginationInput? = null,
        sort: SortInput? = null,
    ): ExpenseConnection {
        val result: PaginatedResult = expenseService.findByFilter(filter, pagination, sort)
        val pageSize = pagination?.first ?: 20
        val edges = result.items.map { expense ->
            ExpenseEdge(
                node = expense,
                cursor = Base64.getEncoder().encodeToString(expense.id.toString().toByteArray()),
            )
        }
        return ExpenseConnection(
            edges = edges,
            pageInfo = PageInfo(
                hasNextPage = result.items.size >= pageSize,
                hasPreviousPage = (pagination?.after != null),
                endCursor = edges.lastOrNull()?.cursor,
                startCursor = edges.firstOrNull()?.cursor,
            ),
            totalCount = result.totalCount,
            totalAmount = result.totalAmount,
        )
    }

    suspend fun expense(id: String): Expense? {
        return expenseService.findById(id.toInt())
    }
}
