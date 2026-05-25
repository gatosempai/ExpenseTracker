package dev.oruizp.expensetracker.backend.database

import org.jetbrains.exposed.sql.Database

object DatabaseFactory {
    fun init(driver: String = "h2", url: String = "jdbc:h2:mem:expense_tracker") {
        Database.connect(url, driver)
    }
}
