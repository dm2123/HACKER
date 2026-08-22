package com.example.hacker.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/** Network state and connectivity status */
enum class NetworkStatus {
    ONLINE,
    OFFLINE,
    UNKNOWN,
    LIMITED
}

/** Checks network connectivity via ConnectivityManager */
class NetworkChecker(private val context: Context) {

    fun getNetworkStatus(): NetworkStatus {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return NetworkStatus.UNKNOWN
            val network = cm.activeNetwork ?: return NetworkStatus.OFFLINE
            val caps = cm.getNetworkCapabilities(network) ?: return NetworkStatus.OFFLINE
            when {
                caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) -> NetworkStatus.ONLINE
                caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) -> NetworkStatus.LIMITED
                else -> NetworkStatus.OFFLINE
            }
        } catch (_: Exception) { NetworkStatus.UNKNOWN }
    }

    fun isInternetAvailable(): Boolean {
        return getNetworkStatus() == NetworkStatus.ONLINE
    }
}
