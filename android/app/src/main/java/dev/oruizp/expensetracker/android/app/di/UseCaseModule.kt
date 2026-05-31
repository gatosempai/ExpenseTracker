package dev.oruizp.expensetracker.android.app.di

import dev.oruizp.expensetracker.android.domain.usecases.expense.AddExpenseUseCase
import dev.oruizp.expensetracker.android.domain.usecases.expense.GetExpensesUseCase
import dev.oruizp.expensetracker.android.domain.usecases.expense.GetTotalExpenseUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetTotalExpenseUseCase(get()) }
    factory { AddExpenseUseCase(get()) }
    factory { GetExpensesUseCase(get()) }
}
