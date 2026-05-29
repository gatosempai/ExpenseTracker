package dev.oruizp.expensetracker.android.data.network

import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class NetworkMonitorImpl(
    private val connectivityManager: ConnectivityManager
) : NetworkMonitor  {
    override fun isOnline(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val caps = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}