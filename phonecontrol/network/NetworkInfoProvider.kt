package com.example.hacker.phonecontrol.network

import android.net.ConnectivityManager
import android.net.NetworkInfo

/** Network information tools */
class NetworkInfoProvider(private val connectivityManager: ConnectivityManager) {

    /** Get active network info */
    fun getActiveNetworkInfo(): NetworkInfo? {
        return connectivityManager.activeNetworkInfo
    }

    /** Is connected to WiFi */
    fun isWifiConnected(): Boolean {
        val info = getActiveNetworkInfo()
        return info?.type == ConnectivityManager.TYPE_WIFI && info.isConnected
    }

    /** Is connected to mobile data */
    fun isMobileConnected(): Boolean {
        val info = getActiveNetworkInfo()
        return info?.type == ConnectivityManager.TYPE_MOBILE && info.isConnected
    }

    /** Is any network connected */
    fun isConnected(): Boolean {
        return connectivityManager.isConnected
    }
}