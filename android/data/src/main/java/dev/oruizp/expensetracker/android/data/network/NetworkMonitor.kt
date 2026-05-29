package dev.oruizp.expensetracker.android.data.network

interface NetworkMonitor {
    fun isOnline(): Boolean
}