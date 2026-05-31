package dev.oruizp.expensetracker.android.app.di

import dev.oruizp.expensetracker.android.features.home.viewmodel.ExpenseViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        ExpenseViewModel(
            get(),
            get(),
            get()
        )
    }
}
