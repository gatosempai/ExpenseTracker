package dev.oruizp.expensetracker.backend.repositories

import dev.oruizp.expensetracker.backend.database.CategoriesTable
import dev.oruizp.expensetracker.backend.database.dbQuery
import dev.oruizp.expensetracker.backend.models.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.*

class CategoryRepositoryImpl : CategoryRepository {

    override suspend fun findAll(): List<Category> = dbQuery {
        CategoriesTable
            .selectAll()
            .orderBy(CategoriesTable.sortOrder)
            .map { it.toCategory() }
    }

    override suspend fun findById(id: Int): Category? = dbQuery {
        CategoriesTable
            .selectAll()
            .where { CategoriesTable.id eq id }
            .singleOrNull()
            ?.toCategory()
    }

    override suspend fun create(input: CreateCategoryInput): Category = dbQuery {
        CategoriesTable.insert {
            it[CategoriesTable.name] = input.name
            it[CategoriesTable.color] = input.color
            it[CategoriesTable.icon] = input.icon ?: "OTHER"
        }.resultedValues?.single()?.toCategory() ?: throw RuntimeException("Failed to create category")
    }

    override suspend fun update(id: Int, input: UpdateCategoryInput): Category? = dbQuery {
        val updated = CategoriesTable.update({ CategoriesTable.id eq id }) {
            input.name?.let { v -> it[CategoriesTable.name] = v }
            input.color?.let { v -> it[CategoriesTable.color] = v }
            input.icon?.let { v -> it[CategoriesTable.icon] = v }
        }
        if (updated > 0) findById(id) else null
    }

    override suspend fun delete(id: Int): Boolean = dbQuery {
        val isDefault = CategoriesTable
            .selectAll()
            .where { CategoriesTable.id eq id }
            .singleOrNull()
            ?.getOrNull(CategoriesTable.isDefault) ?: false
        if (isDefault) return@dbQuery false
        CategoriesTable.deleteWhere { CategoriesTable.id eq id } > 0
    }
}

internal fun ResultRow.toCategory(): Category = Category(
    id = this[CategoriesTable.id],
    name = this[CategoriesTable.name],
    color = this[CategoriesTable.color],
    icon = this[CategoriesTable.icon],
    isDefault = this[CategoriesTable.isDefault],
)
