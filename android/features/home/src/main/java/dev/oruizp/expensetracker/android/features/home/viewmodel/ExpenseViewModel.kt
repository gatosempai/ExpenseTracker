package dev.oruizp.expensetracker.android.features.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.oruizp.expensetracker.android.domain.models.ExpenseCategoryDomain
import dev.oruizp.expensetracker.android.domain.models.ExpenseDomain
import dev.oruizp.expensetracker.android.domain.usecases.expense.AddExpenseUseCase
import dev.oruizp.expensetracker.android.domain.usecases.expense.GetExpensesUseCase
import dev.oruizp.expensetracker.android.domain.usecases.expense.GetTotalExpenseUseCase
import dev.oruizp.expensetracker.android.features.home.models.ExpenseCategoryUi
import dev.oruizp.expensetracker.android.features.home.models.ExpenseUi
import dev.oruizp.expensetracker.android.features.home.state.ExpensesUiState
import dev.oruizp.expensetracker.android.features.home.state.TotalSpentUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExpenseViewModel(
    getTotalSpentUseCase: GetTotalExpenseUseCase,
    private val addExpenseUseCase: AddExpenseUseCase,
    getExpensesUseCase: GetExpensesUseCase
) : ViewModel() {

    val totalSpentUiState: StateFlow<TotalSpentUiState> = getTotalSpentUseCase()
        .map { TotalSpentUiState.Success(it) }
        .catch { TotalSpentUiState.Error(it.message ?: "Unknown error") }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TotalSpentUiState.Loading
        )

    fun addExpense(expenseDomain: ExpenseDomain) {
        viewModelScope.launch {
            addExpenseUseCase(expenseDomain)
        }
    }

    val getExpensesUiState: StateFlow<ExpensesUiState> = getExpensesUseCase()
        .map { expenses ->
            ExpensesUiState.Success(expenses.map { it.toUi() })
        }
        .catch { ExpensesUiState.Error(it.message ?: "Unknown error") }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ExpensesUiState.Loading
        )

    private fun ExpenseDomain.toUi(): ExpenseUi {
        return ExpenseUi(
            id = id,
            title = title,
            amount = amount,
            category = category.toUi(),
            timestamp = timestamp
        )
    }

    private fun ExpenseCategoryDomain.toUi(): ExpenseCategoryUi {
        return when (this) {
            ExpenseCategoryDomain.FOOD -> ExpenseCategoryUi.FOOD
            ExpenseCategoryDomain.TRANSPORT -> ExpenseCategoryUi.TRANSPORT
            ExpenseCategoryDomain.SHOPPING -> ExpenseCategoryUi.SHOPPING
            ExpenseCategoryDomain.BILLS -> ExpenseCategoryUi.BILLS
            ExpenseCategoryDomain.ENTERTAINMENT -> ExpenseCategoryUi.ENTERTAINMENT
            else -> ExpenseCategoryUi.OTHER
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