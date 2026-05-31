package dev.oruizp.expensetracker.android.domain.usecases.expense

import dev.oruizp.expensetracker.android.domain.models.ExpenseDomain
import dev.oruizp.expensetracker.android.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow

class GetExpensesUseCase(
    private val expenseRepository: ExpenseRepository
) {

    operator fun invoke(): Flow<List<ExpenseDomain>> {
        return expenseRepository.getAllExpenses()
    }
}
