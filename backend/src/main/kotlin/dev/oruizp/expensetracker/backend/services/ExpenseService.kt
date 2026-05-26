package dev.oruizp.expensetracker.backend.services

import dev.oruizp.expensetracker.backend.models.*
import dev.oruizp.expensetracker.backend.repositories.ExpenseRepository
import dev.oruizp.expensetracker.backend.repositories.PaginatedResult

class ExpenseService(private val expenseRepository: ExpenseRepository) {

    suspend fun create(input: CreateExpenseInput): Expense {
        if (input.title.isBlank()) throw IllegalArgumentException("Title cannot be empty")
        if (input.amount <= 0) throw IllegalArgumentException("Amount must be greater than 0")
        return expenseRepository.create(input)
    }

    suspend fun findById(id: Int): Expense? = expenseRepository.findById(id)

    suspend fun findByFilter(
        filter: ExpenseFilter?,
        pagination: PaginationInput?,
        sort: SortInput?,
    ): PaginatedResult = expenseRepository.findByFilter(filter, pagination, sort)

    suspend fun update(id: Int, input: UpdateExpenseInput): Expense? {
        val existing = expenseRepository.findById(id) ?: return null
        input.amount?.let { if (it <= 0) throw IllegalArgumentException("Amount must be greater than 0") }
        return expenseRepository.update(id, input)
    }

    suspend fun delete(id: Int): Boolean = expenseRepository.delete(id)
}
