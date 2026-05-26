package dev.oruizp.expensetracker.backend.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.oruizp.expensetracker.backend.config.DatabaseConfig
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {
    private var dataSource: HikariDataSource? = null

    fun init(config: DatabaseConfig) {
        val ds = HikariDataSource(HikariConfig().apply {
            driverClassName = config.driver
            jdbcUrl = config.url
            username = config.user
            password = config.password
            maximumPoolSize = config.poolSize
            minimumIdle = 3
            idleTimeout = 30_000
            connectionTimeout = 10_000
            maxLifetime = 600_000
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        })
        dataSource = ds
        Database.connect(ds)

        if (config.migrationsEnabled) {
            Flyway.configure()
                .dataSource(ds)
                .locations("classpath:db/migration")
                .load()
                .migrate()
        }
    }

    fun close() {
        dataSource?.close()
    }
}
