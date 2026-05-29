package dev.oruizp.expensetracker.android.app

import android.app.Application
import dev.oruizp.expensetracker.android.app.di.appModule
import dev.oruizp.expensetracker.android.app.di.useCaseModule
import dev.oruizp.expensetracker.android.app.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            modules(appModule, useCaseModule, viewModelModule)
        }
    }
}