package dev.oruizp.expensetracker.backend.repositories

import dev.oruizp.expensetracker.backend.models.*

interface CategoryRepository {
    suspend fun findAll(): List<Category>
    suspend fun findById(id: Int): Category?
    suspend fun create(input: CreateCategoryInput): Category
    suspend fun update(id: Int, input: UpdateCategoryInput): Category?
    suspend fun delete(id: Int): Boolean
}
