package dev.oruizp.expensetracker.backend.database

import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

suspend fun <T> dbQuery(block: suspend () -> T): T =
    newSuspendedTransaction { block() }
