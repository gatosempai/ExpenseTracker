package dev.oruizp.expensetracker.backend.plugins

import com.expediagroup.graphql.server.ktor.GraphQL
import com.expediagroup.graphql.server.ktor.graphQLSubscriptionsRoute
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dev.oruizp.expensetracker.backend.graphql.resolvers.*
import dev.oruizp.expensetracker.backend.graphql.subscriptions.BudgetSubscription
import dev.oruizp.expensetracker.backend.services.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private val jackson = jacksonObjectMapper()

fun Application.configureGraphQL(
    expenseService: ExpenseService,
    categoryService: CategoryService,
    budgetService: BudgetService,
    reportService: ReportService,
    syncService: SyncService,
) {
    install(GraphQL) {
        schema {
            packages = listOf("dev.oruizp.expensetracker.backend.graphql", "dev.oruizp.expensetracker.backend.models")
            queries = listOf(
                ExpenseResolver(expenseService),
                CategoryResolver(categoryService),
                BudgetResolver(budgetService),
                ReportResolver(reportService),
                DashboardResolver(reportService, budgetService),
                SyncResolver(syncService),
            )
            mutations = listOf(
                ExpenseMutationResolver(expenseService),
                CategoryMutationResolver(categoryService),
                BudgetMutationResolver(budgetService),
            )
            subscriptions = listOf(
                BudgetSubscription(budgetService),
            )
        }
    }

    routing {
        val graphQLPlugin = application.plugin(GraphQL)

        post("/graphql") {
            graphQLPlugin.server.execute(call.request)?.let { response ->
                call.respondText(jackson.writeValueAsString(response), ContentType.Application.Json)
            } ?: call.respond(HttpStatusCode.BadRequest)
        }

        get("/graphql") {
            graphQLPlugin.server.execute(call.request)?.let { response ->
                call.respondText(jackson.writeValueAsString(response), ContentType.Application.Json)
            } ?: call.respond(HttpStatusCode.BadRequest)
        }

        graphQLSubscriptionsRoute()
    }
}
