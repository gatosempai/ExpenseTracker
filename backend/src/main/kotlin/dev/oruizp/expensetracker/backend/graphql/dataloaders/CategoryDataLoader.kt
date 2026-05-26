package dev.oruizp.expensetracker.backend.graphql.dataloaders

import dev.oruizp.expensetracker.backend.models.Category
import dev.oruizp.expensetracker.backend.services.CategoryService
import kotlinx.coroutines.runBlocking
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import java.util.concurrent.CompletableFuture

fun createCategoryDataLoader(categoryService: CategoryService): DataLoader<Int, Category?> {
    return DataLoader.newDataLoader<Int, Category?> { ids ->
        CompletableFuture.supplyAsync {
            val categories = runBlocking { categoryService.findAll() }
            val map = categories.associateBy { it.id }
            ids.map { map[it] }
        }
    }
}

fun registerDataLoaders(registry: DataLoaderRegistry, categoryService: CategoryService) {
    registry.register("category", createCategoryDataLoader(categoryService))
}
