package dev.oruizp.expensetracker.backend

import dev.oruizp.expensetracker.backend.config.AppConfig
import dev.oruizp.expensetracker.backend.di.configureDataRoutesModule
import dev.oruizp.expensetracker.backend.di.configureGraphQLModule
import dev.oruizp.expensetracker.backend.di.configureKoin
import dev.oruizp.expensetracker.backend.plugins.configureCORS
import dev.oruizp.expensetracker.backend.plugins.configureMonitoring
import dev.oruizp.expensetracker.backend.plugins.configureStatusPages
import dev.oruizp.expensetracker.backend.routes.registerHealthRoutes
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.websocket.*

fun main() {
    val config = AppConfig()
    embeddedServer(
        Netty,
        port = config.server.port,
        host = config.server.host,
        module = Application::module,
    ).start(wait = true)
}

fun Application.module() {
    val config = AppConfig()

    install(ContentNegotiation) {
        json()
    }

    install(WebSockets)

    configureStatusPages()
    configureMonitoring()
    configureCORS(config.server)

    configureKoin()
    configureGraphQLModule()

    registerHealthRoutes()
    configureDataRoutesModule()
}
