package dev.oruizp.expensetracker.backend.database

import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway

fun runMigrations(dataSource: HikariDataSource) {
    Flyway.configure()
        .dataSource(dataSource)
        .locations("classpath:db/migration")
        .load()
        .migrate()
}
