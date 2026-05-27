package dev.oruizp.expensetracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ExpenseCategory {
    FOOD,
    TRANSPORT,
    SHOPPING,
    BILLS,
    ENTERTAINMENT,
    OTHER
}

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: ExpenseCategory,
    val timestamp: Long = System.currentTimeMillis()
)
