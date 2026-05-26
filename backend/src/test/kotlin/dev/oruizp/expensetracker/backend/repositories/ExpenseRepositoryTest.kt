package dev.oruizp.expensetracker.backend.repositories

import dev.oruizp.expensetracker.backend.models.*
import dev.oruizp.expensetracker.backend.testutil.MockFactories
import dev.oruizp.expensetracker.backend.testutil.TestDatabase
import kotlinx.coroutines.runBlocking
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import kotlin.test.*

class ExpenseRepositoryTest {

    private val repository = ExpenseRepositoryImpl()

    @Test
    fun `create expense persists to database`() = runBlocking {
        val expense = repository.create(MockFactories.createExpenseInput(categoryId = "1"))
        assertEquals("Test Expense", expense.title)
        assertTrue(expense.id > 0)
    }

    @Test
    fun `find by id returns expense when exists`() = runBlocking {
        val created = repository.create(MockFactories.createExpenseInput(categoryId = "1"))
        val found = repository.findById(created.id)
        assertNotNull(found)
        assertEquals(created.title, found?.title)
    }

    @Test
    fun `find by id returns null when not found`() = runBlocking {
        val found = repository.findById(999)
        assertNull(found)
    }

    @Test
    fun `delete marks expense as deleted`() = runBlocking {
        val created = repository.create(MockFactories.createExpenseInput(categoryId = "1"))
        val deleted = repository.delete(created.id)
        assertTrue(deleted)
        val found = repository.findById(created.id)
        assertNull(found)
    }

    companion object {
        @BeforeClass
        @JvmStatic
        fun setup() {
            TestDatabase.setup()
        }
    }
}
