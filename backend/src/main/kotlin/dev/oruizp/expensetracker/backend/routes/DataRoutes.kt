package dev.oruizp.expensetracker.backend.routes

import dev.oruizp.expensetracker.backend.models.CreateExpenseInput
import dev.oruizp.expensetracker.backend.models.ExpenseFilter
import dev.oruizp.expensetracker.backend.services.ExpenseService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Application.registerDataRoutes(expenseService: ExpenseService) {
    routing {
        post("/data/export") {
            val body = call.receive<ExportRequestBody>()
            val filter = ExpenseFilter(dateFrom = body.dateFrom, dateTo = body.dateTo)
            val result = expenseService.findByFilter(filter, null, null)
            val csv = buildString {
                appendLine("id,title,amount,categoryId,date,description")
                for (expense in result.items) {
                    appendLine("${expense.id},${expense.title},${expense.amount},${expense.categoryId},${expense.date},${expense.description.orEmpty()}")
                }
            }
            call.response.header(HttpHeaders.ContentDisposition, "attachment; filename=expenses.csv")
            call.respondText(csv, ContentType.Text.CSV)
        }
    }
}

@Serializable
data class ExportRequestBody(
    val dateFrom: String? = null,
    val dateTo: String? = null,
)
