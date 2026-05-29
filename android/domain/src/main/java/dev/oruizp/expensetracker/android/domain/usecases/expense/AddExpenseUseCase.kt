package dev.oruizp.expensetracker.android.domain.usecases.expense

import dev.oruizp.expensetracker.android.domain.models.Expense
import dev.oruizp.expensetracker.android.domain.repository.ExpenseRepository

class AddExpenseUseCase(
    private val expenseRepository: ExpenseRepository
) {

    suspend operator fun invoke(expense: Expense) {
        return expenseRepository.addExpense(expense)
    }
}