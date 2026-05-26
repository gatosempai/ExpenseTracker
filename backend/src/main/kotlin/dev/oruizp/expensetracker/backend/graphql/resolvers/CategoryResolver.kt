package dev.oruizp.expensetracker.backend.graphql.resolvers

import com.expediagroup.graphql.server.operations.Query
import dev.oruizp.expensetracker.backend.models.*
import dev.oruizp.expensetracker.backend.services.CategoryService

class CategoryResolver(private val categoryService: CategoryService) : Query {

    suspend fun categories(): List<Category> {
        return categoryService.findAll()
    }

    suspend fun category(id: String): Category? {
        return categoryService.findById(id.toInt())
    }

    suspend fun categoryCreate(input: CreateCategoryInput): Category {
        return categoryService.create(input)
    }

    suspend fun categoryUpdate(id: String, input: UpdateCategoryInput): Category? {
        return categoryService.update(id.toInt(), input)
    }

    suspend fun categoryDelete(id: String): DeleteResult {
        val success = categoryService.delete(id.toInt())
        return DeleteResult(success = success, id = id)
    }
}
