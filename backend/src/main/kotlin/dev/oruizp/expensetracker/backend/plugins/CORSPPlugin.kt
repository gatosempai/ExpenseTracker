package dev.oruizp.expensetracker.backend.plugins

import dev.oruizp.expensetracker.backend.config.ServerConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import java.net.URI

fun Application.configureCORS(config: ServerConfig) {
    install(CORS) {
        for (origin in config.corsAllowedOrigins) {
            val schemes = mutableListOf("http", "https")
            val host = try {
                val uri = URI(origin.trim())
                if (uri.scheme != null) schemes.clear()
                schemes.add(uri.scheme ?: "http")
                uri.host ?: origin.trim()
            } catch (_: Exception) {
                origin.trim()
            }
            allowHost(host, schemes)
        }
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowCredentials = true
    }
}
