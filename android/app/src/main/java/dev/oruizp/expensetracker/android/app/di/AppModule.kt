package dev.oruizp.expensetracker.android.app.di

import android.content.Context
import android.net.ConnectivityManager
import dev.oruizp.expensetracker.android.data.local.ExpenseDatabase
import dev.oruizp.expensetracker.android.data.network.NetworkMonitor
import dev.oruizp.expensetracker.android.data.network.NetworkMonitorImpl
import dev.oruizp.expensetracker.android.data.remote.GraphqlModule
import dev.oruizp.expensetracker.android.data.remote.expense.ExpenseApi
import dev.oruizp.expensetracker.android.data.remote.expense.ExpenseApiGraphQL
import dev.oruizp.expensetracker.android.data.repository.expense.ExpenseRepositoryImpl
import dev.oruizp.expensetracker.android.domain.repository.ExpenseRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single<ConnectivityManager> {
        androidContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    single<NetworkMonitor> { NetworkMonitorImpl(get()) }

    single { ExpenseDatabase.getDatabase(androidContext()) }

    single { get<ExpenseDatabase>().expenseDao() }

    single { GraphqlModule.provideApolloClient(androidContext()) }

    single<ExpenseApi> { ExpenseApiGraphQL(get()) }

    single<ExpenseRepository> { ExpenseRepositoryImpl(get(), get(), get()) }
}
