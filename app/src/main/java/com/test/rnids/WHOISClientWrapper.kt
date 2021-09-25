package com.test.rnids

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.apache.commons.net.whois.WhoisClient
import java.io.IOException

import android.os.AsyncTask
import java.lang.Runnable
import java.util.concurrent.Executor

class WHOISTask(result: MutableLiveData<String>, lastDomain: MutableLiveData<String>,
                blockingCallback: ((String) -> Unit)? = null)

    : AsyncTask<String, Void, Void>() {

    private val _result : MutableLiveData<String> = result
    private val _lastDomain : MutableLiveData<String> = lastDomain

    private val _blockingCallback : ((String) -> Unit)? = blockingCallback

    private var _response = ""

    companion object {
        @JvmStatic lateinit var client: WhoisClient
    }

    init {
        client = WhoisClient()
    }

    override fun doInBackground(vararg params: String?) : Void? {
        try {
            client.connect(params[1])

            _response = client.query(params[0])
            _result.postValue(_response)

            client.disconnect()

            _lastDomain.postValue(params[0]!!)

            _blockingCallback?.invoke(_response)
        } catch (e: IOException) {
            _response = e.message!!
            _result.postValue(_response)
        }

        return null
    }
}

class WHOISClient {
    val lastDomain = MutableLiveData("")
    val result = MutableLiveData("")

    fun query(domain: LiveData<String>, server: LiveData<String>? = null,
              blockingCallback: ((String) -> Unit)? = null)
    {
        var serverToUse = server
        if (serverToUse == null)
        {
            serverToUse = MutableLiveData("whois.iana.org")
        }

        if (domain.value.isNullOrEmpty() || serverToUse.value.isNullOrEmpty())
        {
            return
        }

        val task = WHOISTask(result, lastDomain, blockingCallback)
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,domain.value, serverToUse.value)
    }
}