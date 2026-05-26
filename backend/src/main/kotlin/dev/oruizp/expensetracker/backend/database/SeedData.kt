package dev.oruizp.expensetracker.backend.database

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object SeedData {
    private val defaultCategories = listOf(
        Triple("Food", "#FF5252", "FOOD"),
        Triple("Transport", "#448AFF", "TRANSPORT"),
        Triple("Shopping", "#FFAB40", "SHOPPING"),
        Triple("Bills", "#7C4DFF", "BILLS"),
        Triple("Entertainment", "#E040FB", "ENTERTAINMENT"),
        Triple("Other", "#9E9E9E", "OTHER"),
    )

    fun seedIfEmpty() {
        transaction {
            if (CategoriesTable.selectAll().empty().not()) return@transaction
            for ((index, cat) in defaultCategories.withIndex()) {
                CategoriesTable.insert { stmt ->
                    stmt[CategoriesTable.name] = cat.first
                    stmt[CategoriesTable.color] = cat.second
                    stmt[CategoriesTable.icon] = cat.third
                    stmt[CategoriesTable.isDefault] = true
                    stmt[CategoriesTable.sortOrder] = index
                }
            }
        }
    }
}
