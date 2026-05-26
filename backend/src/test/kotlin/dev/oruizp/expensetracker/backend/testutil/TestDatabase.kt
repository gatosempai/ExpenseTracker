package dev.oruizp.expensetracker.backend.testutil

import dev.oruizp.expensetracker.backend.database.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

object TestDatabase {
    fun setup() {
        Database.connect(
            "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
            driver = "org.h2.Driver",
        )
        transaction {
            SchemaUtils.create(
                UsersTable,
                CategoriesTable,
                ExpensesTable,
                BudgetsTable,
                SyncLogTable,
            )
            UsersTable.insert { stmt ->
                stmt[UsersTable.email] = "test@test.com"
                stmt[UsersTable.passwordHash] = "test"
                stmt[UsersTable.displayName] = "Test"
            }
            CategoriesTable.insert { stmt ->
                stmt[CategoriesTable.name] = "Test"
                stmt[CategoriesTable.color] = "#FF0000"
                stmt[CategoriesTable.icon] = "TEST"
                stmt[CategoriesTable.isDefault] = true
            }
        }
    }
}
