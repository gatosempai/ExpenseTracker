package dev.oruizp.expensetracker.backend.services

import dev.oruizp.expensetracker.backend.repositories.ExpenseRepositoryImpl
import dev.oruizp.expensetracker.backend.testutil.MockFactories
import dev.oruizp.expensetracker.backend.testutil.TestDatabase
import kotlinx.coroutines.runBlocking
import org.junit.BeforeClass
import org.junit.Test
import kotlin.test.*

class ExpenseServiceTest {

    private val service = ExpenseService(ExpenseRepositoryImpl())

    @Test
    fun `create expense with empty title throws validation error`() = runBlocking {
        val input = MockFactories.createExpenseInput(title = "", categoryId = "1")
        try {
            service.create(input)
            throw AssertionError("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            // expected
        }
    }

    @Test
    fun `create expense with negative amount throws validation error`() = runBlocking {
        val input = MockFactories.createExpenseInput(amount = -10.0, categoryId = "1")
        try {
            service.create(input)
            throw AssertionError("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            // expected
        }
    }

    @Test
    fun `create expense persists correctly`() = runBlocking {
        val expense = service.create(MockFactories.createExpenseInput(categoryId = "1"))
        assertEquals("Test Expense", expense.title)
        assertTrue(expense.id > 0)
    }

    companion object {
        @BeforeClass
        @JvmStatic
        fun setup() {
            TestDatabase.setup()
        }
    }
}
