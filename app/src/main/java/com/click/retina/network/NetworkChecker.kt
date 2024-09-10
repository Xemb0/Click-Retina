package com.click.retina.network

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.annotation.RequiresPermission

class NetworkChecker(private val connectivityManager: ConnectivityManager) {

    private var onNetworkAvailable: (() -> Unit)? = null
    private var onNetworkLost: (() -> Unit)? = null

    init {
        registerNetworkCallback()
    }

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    private fun registerNetworkCallback() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                onNetworkAvailable?.invoke()
            }

            override fun onLost(network: Network) {
                onNetworkLost?.invoke()
            }
        })
    }

    fun setOnNetworkAvailable(action: () -> Unit) {
        onNetworkAvailable = action
    }

    fun setOnNetworkLost(action: () -> Unit) {
        onNetworkLost = action
    }

    val hasValidInternet: Boolean
        get() {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
        }
}
