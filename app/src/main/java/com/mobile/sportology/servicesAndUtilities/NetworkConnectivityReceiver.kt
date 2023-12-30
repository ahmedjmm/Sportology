package com.mobile.sportology.servicesAndUtilities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.mobile.sportology.Shared

class NetworkConnectivityReceiver: BroadcastReceiver() {
    private var networkStateListener: NetworkStateListener? = null
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
            Shared.isConnected = isConnected
            this.networkStateListener?.onNetworkStateChanged(isConnected)
        }
    }

    fun setListener(networkStateListener: NetworkStateListener?) {
        this.networkStateListener = networkStateListener
    }

    interface NetworkStateListener {
        fun onNetworkStateChanged(isConnected: Boolean)
    }
}