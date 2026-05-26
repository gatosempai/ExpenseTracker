package dev.oruizp.expensetracker.backend.di

import dev.oruizp.expensetracker.backend.config.AppConfig
import dev.oruizp.expensetracker.backend.database.DatabaseFactory
import dev.oruizp.expensetracker.backend.database.SeedData
import dev.oruizp.expensetracker.backend.plugins.configureGraphQL
import dev.oruizp.expensetracker.backend.repositories.*
import dev.oruizp.expensetracker.backend.routes.registerDataRoutes
import dev.oruizp.expensetracker.backend.services.*
import io.ktor.server.application.*
import org.koin.core.context.GlobalContext
import org.koin.dsl.module
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin

fun Application.configureKoin() {
    val appConfig = AppConfig()

    install(Koin) {
        modules(
            module {
                single { appConfig }
                single { appConfig.database }

                single { DatabaseFactory.apply { init(get()) } }

                single<ExpenseRepository> { ExpenseRepositoryImpl() }
                single<CategoryRepository> { CategoryRepositoryImpl() }
                single<BudgetRepository> { BudgetRepositoryImpl() }
                single<SyncRepository> { SyncRepositoryImpl() }

                single { ExpenseService(get()) }
                single { CategoryService(get()) }
                single { BudgetService(get(), get()) }
                single { ReportService(get(), get()) }
                single { SyncService(get(), get(), get(), get()) }
            }
        )
    }

    get<DatabaseFactory>()
    SeedData.seedIfEmpty()
}

fun Application.configureGraphQLModule() {
    val koin = GlobalContext.get()
    val expenseService: ExpenseService = koin.get()
    val categoryService: CategoryService = koin.get()
    val budgetService: BudgetService = koin.get()
    val reportService: ReportService = koin.get()
    val syncService: SyncService = koin.get()

    configureGraphQL(expenseService, categoryService, budgetService, reportService, syncService)
}

fun Application.configureDataRoutesModule() {
    val koin = GlobalContext.get()
    val expenseService: ExpenseService = koin.get()
    registerDataRoutes(expenseService)
}
