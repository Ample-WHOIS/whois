package com.test.rnids.util

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.locks.ReentrantLock
import android.net.NetworkInfo

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build


fun hasNetworkAccess(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val n = cm.activeNetwork
        if (n != null) {
            val nc = cm.getNetworkCapabilities(n)
            return nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || nc.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        }
        return false
    } else {
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }
}

fun isHostAvailable(context: Context, host: String?, port: Int, timeout: Int,
                    callback: ((Boolean) -> Unit)) {
    var resp = false
    if (hasNetworkAccess(context))
    {
        try {
            Socket().use { socket ->
                val inetAddress = InetAddress.getByName(host)
                val inetSocketAddress =
                    InetSocketAddress(inetAddress, port)
                socket.connect(inetSocketAddress, timeout)
                resp = true
            }
        } catch (e: IOException) {
            resp = false
        }
    }

    callback.invoke(resp)
}

fun validateDomain(str: String) : Boolean
{
    if (str.isEmpty())
    {
        return false
    }

    val validationRegex =
        Regex("((?!-))(xn--)?[a-z0-9][a-z0-9-_]{0,61}[a-z0-9]?\\.(xn--)?([a-z0-9\\-]{1,61}|[a-z0-9-]{1,30}\\.[a-z]{2,})$")
    val matches = validationRegex.findAll(str)

    var count = 0
    for (match in matches)
    {
        if (++count > 1)
        {
            break
        }
    }

    if (count != 1)
    {
        return false
    }
    return true
}