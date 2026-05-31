package dev.oruizp.expensetracker.android.data.repository.expense

import dev.oruizp.expensetracker.android.data.local.Expense
import dev.oruizp.expensetracker.android.data.local.ExpenseCategoryData
import dev.oruizp.expensetracker.android.data.local.ExpenseDao
import dev.oruizp.expensetracker.android.data.network.NetworkMonitor
import dev.oruizp.expensetracker.android.data.remote.expense.ExpenseApi
import dev.oruizp.expensetracker.android.domain.models.ExpenseCategoryDomain
import dev.oruizp.expensetracker.android.domain.models.ExpenseDomain
import dev.oruizp.expensetracker.android.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Locale

class ExpenseRepositoryImpl(
    private val expenseDao: ExpenseDao,
    private val remoteApi: ExpenseApi,
    private val networkMonitor: NetworkMonitor
) : ExpenseRepository {

    override fun getAllExpenses(): Flow<List<ExpenseDomain>> {
        return expenseDao.getAllExpenses().map { roomExpenses ->
            if (networkMonitor.isOnline()) {
                try {
                    val remoteExpenses = remoteApi.getExpenses()
                    roomExpenses.map { room ->
                        remoteExpenses.find { it.id.toLong() == room.id }?.let { dto ->
                            Expense(
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

    override suspend fun addExpense(expense: ExpenseDomain) {
        val roomExpense = Expense(
            title = expense.title,
            amount = expense.amount,
            category = expense.category.toData(),
            timestamp = expense.timestamp,
        )
        expenseDao.insertExpense(roomExpense)
    }

    override suspend fun deleteExpense(expense: ExpenseDomain) {
        val roomExpense = Expense(
            id = expense.id,
            title = expense.title,
            amount = expense.amount,
            category = expense.category.toData(),
            timestamp = expense.timestamp,
        )
        expenseDao.deleteExpense(roomExpense)
    }

    override fun getTotalSpent(): Flow<Double> {
        return expenseDao.getTotalSpent().map { it ?: 0.0 }
    }

    private fun mapCategoryId(categoryId: Int): ExpenseCategoryData {
        return when (categoryId) {
            1 -> ExpenseCategoryData.FOOD
            2 -> ExpenseCategoryData.TRANSPORT
            3 -> ExpenseCategoryData.SHOPPING
            4 -> ExpenseCategoryData.BILLS
            5 -> ExpenseCategoryData.ENTERTAINMENT
            else -> ExpenseCategoryData.OTHER
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

    private fun Expense.toDomain(): ExpenseDomain {
        return ExpenseDomain(
            id = id,
            title = title,
            amount = amount,
            category = category.toDomain(),
            timestamp = timestamp,
        )
    }

    private fun ExpenseCategoryData.toDomain(): ExpenseCategoryDomain {
        return when (this) {
            ExpenseCategoryData.FOOD -> ExpenseCategoryDomain.FOOD
            ExpenseCategoryData.TRANSPORT -> ExpenseCategoryDomain.TRANSPORT
            ExpenseCategoryData.SHOPPING -> ExpenseCategoryDomain.SHOPPING
            ExpenseCategoryData.BILLS -> ExpenseCategoryDomain.BILLS
            ExpenseCategoryData.ENTERTAINMENT -> ExpenseCategoryDomain.ENTERTAINMENT
            else -> ExpenseCategoryDomain.OTHER
        }
    }

    private fun ExpenseCategoryDomain.toData(): ExpenseCategoryData {
        return when (this) {
            ExpenseCategoryDomain.FOOD -> ExpenseCategoryData.FOOD
            ExpenseCategoryDomain.TRANSPORT -> ExpenseCategoryData.TRANSPORT
            ExpenseCategoryDomain.SHOPPING -> ExpenseCategoryData.SHOPPING
            ExpenseCategoryDomain.BILLS -> ExpenseCategoryData.BILLS
            ExpenseCategoryDomain.ENTERTAINMENT -> ExpenseCategoryData.ENTERTAINMENT
            else -> ExpenseCategoryData.OTHER
        }
    }
}
