package dev.oruizp.expensetracker.android.features.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.oruizp.expensetracker.android.domain.models.Expense
import dev.oruizp.expensetracker.android.domain.usecases.expense.AddExpenseUseCase
import dev.oruizp.expensetracker.android.domain.usecases.expense.GetTotalExpenseUseCase
import dev.oruizp.expensetracker.android.features.home.data.TotalSpentUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExpenseViewModel(
    private val getTotalSpentUseCase: GetTotalExpenseUseCase,
    private val addExpenseUseCase: AddExpenseUseCase,
) : ViewModel() {

    val totalSpentUiState: StateFlow<TotalSpentUiState> = getTotalSpentUseCase()
        .map { TotalSpentUiState.Success(it) }
        .catch { TotalSpentUiState.Error(it.message ?: "Unknown error") }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TotalSpentUiState.Loading
        )

    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            addExpenseUseCase(expense)
        }
    }

    /*private val expenseDao = ExpenseDatabase.Companion.getDatabase(application).expenseDao()

    val expenses: StateFlow<List<Expense>> = expenseDao.getAllExpenses()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val totalSpent: StateFlow<Double> = expenseDao.getTotalSpent()
        .map { it ?: 0.0 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = 0.0
        )

    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            expenseDao.insertExpense(expense)
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            expenseDao.deleteExpense(expense)
        }
    }*/

}