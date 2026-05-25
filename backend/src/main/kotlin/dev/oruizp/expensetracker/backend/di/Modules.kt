package dev.oruizp.expensetracker.backend.di

import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.dsl.module

fun Application.configureKoin() {
    install(Koin) {
        modules(
            module {
                // Add your Koin modules here
            }
        )
    }
}
