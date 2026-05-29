package dev.oruizp.expensetracker.android.data.repository.category

import dev.oruizp.expensetracker.data.graphql.type.Category

interface CategoryRepository {
    suspend fun getCategories(): List<Category>
    suspend fun getCategory(id: String): Category?
    suspend fun createCategory(category: Category): Category
    suspend fun updateCategory(id: String, category: Category): Category?
    suspend fun deleteCategory(id: String): Boolean
}