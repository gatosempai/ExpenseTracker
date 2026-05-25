package dev.oruizp.expensetracker.backend.database

import org.jetbrains.exposed.sql.Table

object Expenses : Table() {
    val id = long("id").autoIncrement()
    val title = varchar("title", 255)
    val amount = double("amount")
    val category = varchar("category", 100)
    val date = varchar("date", 50)

    override val primaryKey = PrimaryKey(id)
}
