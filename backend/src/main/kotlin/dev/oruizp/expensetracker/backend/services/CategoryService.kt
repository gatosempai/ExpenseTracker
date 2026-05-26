package dev.oruizp.expensetracker.backend.services

import dev.oruizp.expensetracker.backend.models.*
import dev.oruizp.expensetracker.backend.repositories.CategoryRepository

class CategoryService(private val categoryRepository: CategoryRepository) {

    suspend fun findAll(): List<Category> = categoryRepository.findAll()

    suspend fun findById(id: Int): Category? = categoryRepository.findById(id)

    suspend fun create(input: CreateCategoryInput): Category {
        if (input.name.isBlank()) throw IllegalArgumentException("Category name cannot be empty")
        return categoryRepository.create(input)
    }

    suspend fun update(id: Int, input: UpdateCategoryInput): Category? {
        val existing = categoryRepository.findById(id) ?: return null
        return categoryRepository.update(id, input)
    }

    suspend fun delete(id: Int): Boolean {
        val category = categoryRepository.findById(id) ?: return false
        if (category.isDefault) throw IllegalArgumentException("Cannot delete default category")
        return categoryRepository.delete(id)
    }
}
