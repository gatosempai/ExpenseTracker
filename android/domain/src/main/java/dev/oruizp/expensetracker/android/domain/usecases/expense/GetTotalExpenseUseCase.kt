package dev.oruizp.expensetracker.android.domain.usecases.expense

import dev.oruizp.expensetracker.android.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTotalExpenseUseCase(
    private val expenseRepository: ExpenseRepository
) {

    operator fun invoke(): Flow<String> {
        return expenseRepository.getAllExpenses()
            .map { expenses ->
                expenses.sumOf { it.amount }
            }
            .map { total ->
                total.toString()
            }
    }
}
