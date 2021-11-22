package vadiole.core.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import android.os.Build

val Context.hasNetworkConnection: Boolean
    @SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    get() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager? ?: return false
                val activeNetworks = cm.activeNetwork
                val capabilities = cm.getNetworkCapabilities(activeNetworks) ?: return false
                val transports = arrayOf(
                    TRANSPORT_CELLULAR,
                    TRANSPORT_WIFI,
                    TRANSPORT_BLUETOOTH,
                    TRANSPORT_ETHERNET,
                    TRANSPORT_VPN,
                )
                return transports.any { capabilities.hasTransport(it) }
            } catch (e: Exception) {
                e.printStackTrace()
                return true
            }
        } else {
            try {
                val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                var netInfo = connectivityManager.activeNetworkInfo
                if (netInfo != null && (netInfo.isConnectedOrConnecting || netInfo.isAvailable)) {
                    return true
                }
                netInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                if (netInfo != null && netInfo.isConnectedOrConnecting) {
                    return true
                } else {
                    netInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                    if (netInfo != null && netInfo.isConnectedOrConnecting) {
                        return true
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return true
            }
            return false
        }
    }