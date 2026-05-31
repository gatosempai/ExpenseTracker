package dev.oruizp.expensetracker.android.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ExpenseCategoryData {
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
    val category: ExpenseCategoryData,
    val timestamp: Long = System.currentTimeMillis()
)
