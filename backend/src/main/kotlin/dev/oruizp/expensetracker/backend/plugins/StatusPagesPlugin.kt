package dev.oruizp.expensetracker.backend.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            val msg: String = cause.message ?: "Validation failed"
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = mapOf<String, String>(
                    "code" to "VALIDATION_ERROR",
                    "message" to msg,
                )
            )
        }
        exception<NoSuchElementException> { call, cause ->
            val msg: String = cause.message ?: "Resource not found"
            call.respond(
                status = HttpStatusCode.NotFound,
                message = mapOf<String, String>(
                    "code" to "NOT_FOUND",
                    "message" to msg,
                )
            )
        }
        exception<Throwable> { call, cause ->
            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = mapOf<String, String>(
                    "code" to "INTERNAL_ERROR",
                    "message" to "An unexpected error occurred",
                )
            )
        }
    }
}
