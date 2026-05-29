package dev.oruizp.expensetracker.android.data.repository.expense

import dev.oruizp.expensetracker.android.data.local.ExpenseCategory
import dev.oruizp.expensetracker.android.data.local.ExpenseDao
import dev.oruizp.expensetracker.android.data.network.NetworkMonitor
import dev.oruizp.expensetracker.android.data.remote.expense.ExpenseApi
import dev.oruizp.expensetracker.android.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Locale
import dev.oruizp.expensetracker.android.data.local.Expense as RoomExpense
import dev.oruizp.expensetracker.android.domain.models.Expense as DomainExpense

class ExpenseRepositoryImpl(
    private val expenseDao: ExpenseDao,
    private val remoteApi: ExpenseApi,
    private val networkMonitor: NetworkMonitor
) : ExpenseRepository {

    override fun getAllExpenses(): Flow<List<DomainExpense>> {
        return expenseDao.getAllExpenses().map { roomExpenses ->
            if (networkMonitor.isOnline()) {
                try {
                    val remoteExpenses = remoteApi.getExpenses()
                    roomExpenses.map { room ->
                        remoteExpenses.find { it.id.toLong() == room.id }?.let { dto ->
                            RoomExpense(
                                id = dto.id.toLong(),
                                title = dto.title,
                                amount = dto.amount,
                                category = mapCategoryId(dto.categoryId),
                                timestamp = parseDate(dto.date),
                            )
                        } ?: room
                    }
                } catch (_: Exception) {
                    roomExpenses
                }
            } else {
                roomExpenses
            }
        }.map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun addExpense(expense: DomainExpense) {
        val roomExpense = RoomExpense(
            title = expense.title,
            amount = expense.amount,
            category = ExpenseCategory.valueOf(expense.category.uppercase()),
            timestamp = expense.timestamp,
        )
        expenseDao.insertExpense(roomExpense)
    }

    override suspend fun deleteExpense(expense: DomainExpense) {
        val roomExpense = RoomExpense(
            id = expense.id,
            title = expense.title,
            amount = expense.amount,
            category = ExpenseCategory.valueOf(expense.category.uppercase()),
            timestamp = expense.timestamp,
        )
        expenseDao.deleteExpense(roomExpense)
    }

    override fun getTotalSpent(): Flow<Double> {
        return expenseDao.getTotalSpent().map { it ?: 0.0 }
    }

    private fun mapCategoryId(categoryId: Int): ExpenseCategory {
        return when (categoryId) {
            1 -> ExpenseCategory.FOOD
            2 -> ExpenseCategory.TRANSPORT
            3 -> ExpenseCategory.SHOPPING
            4 -> ExpenseCategory.BILLS
            5 -> ExpenseCategory.ENTERTAINMENT
            else -> ExpenseCategory.OTHER
        }
    }

    private fun parseDate(date: String): Long {
        return try {
            val formats = listOf(
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()),
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()),
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()),
            )
            for (format in formats) {
                try {
                    return format.parse(date)?.time ?: System.currentTimeMillis()
                } catch (_: Exception) { }
            }
            System.currentTimeMillis()
        } catch (_: Exception) {
            System.currentTimeMillis()
        }
    }

    private fun RoomExpense.toDomain(): DomainExpense {
        return DomainExpense(
            id = id,
            title = title,
            amount = amount,
            category = category.name,
            timestamp = timestamp,
        )
    }
}
