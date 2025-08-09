package com.example.doitlist.utils

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.RequiresPermission

object NetworkUtils {
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val nw = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(nw) ?: return false
        return when {
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)   -> true
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)-> true
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)-> true
            else                                                  -> false
        }
    }
}