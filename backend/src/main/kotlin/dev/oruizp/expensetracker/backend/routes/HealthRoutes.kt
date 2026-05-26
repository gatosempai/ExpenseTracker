package dev.oruizp.expensetracker.backend.routes

import io.ktor.server.application.*
import io.ktor.server.response.respondText
import io.ktor.server.routing.*

fun Application.registerHealthRoutes() {
    routing {
        get("/health") {
            val json = "{\"status\":\"UP\",\"version\":\"1.0.0\"}"
            call.respondText(json, io.ktor.http.ContentType.Application.Json)
        }
    }
}
