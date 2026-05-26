package dev.oruizp.expensetracker.backend.graphql.resolvers

import com.expediagroup.graphql.server.operations.Mutation
import dev.oruizp.expensetracker.backend.models.*
import dev.oruizp.expensetracker.backend.services.CategoryService

class CategoryMutationResolver(private val categoryService: CategoryService) : Mutation {

    suspend fun createCategory(input: CreateCategoryInput): Category {
        return categoryService.create(input)
    }

    suspend fun updateCategory(id: String, input: UpdateCategoryInput): Category? {
        return categoryService.update(id.toInt(), input)
    }

    suspend fun deleteCategory(id: String): DeleteResult {
        val success = categoryService.delete(id.toInt())
        return DeleteResult(success = success, id = id)
    }
}
