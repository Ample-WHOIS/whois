package com.test.rnids

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.apache.commons.net.whois.WhoisClient
import java.io.IOException

import android.os.AsyncTask
import com.test.rnids.parsers.BasicParser
import com.test.rnids.providers.IANAProvider
import java.lang.Runnable
import java.util.concurrent.Executor
import java.util.concurrent.locks.ReentrantLock

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

class WHOISClientWrapper(appContext: Context) {
    val lastDomain = MutableLiveData("")
    val result = MutableLiveData("")

    private val lock = ReentrantLock()
    private val condition = lock.newCondition()

    private val _appContext = appContext

    fun query(domain: String, server: String = "whois.iana.org",
              blockingCallback: ((String) -> Unit)? = null)
    {
        if (domain.isEmpty() || server.isEmpty())
        {
            return
        }

        val task = WHOISTask(result, lastDomain, blockingCallback)
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, domain, server)
    }

    fun queryChain(domain: String, server: String = "whois.iana.org")
    {
        if (domain.isEmpty())
        {
            return
        }

        var prevLink = server
        while (true)
        {
            var response: String = ""
            query(domain, prevLink) { resp ->
                response = resp

                lock.lock()
                try {
                    condition.signalAll()
                } finally {
                    lock.unlock()
                }
            }

            lock.lock()
            try {
                condition.await()
            } finally {
                lock.unlock()
            }

            val parser = BasicParser(_appContext)
            parser.processRaw(response)

            var result = ""
            val metaSection = parser.getSection("meta")!!

            result = metaSection.getKey("refer")

            if (result.isNotEmpty())
            {
                prevLink = result
            }

            result = metaSection.getKey("whois")

            if (result.isNotEmpty())
            {
                prevLink = result
            }

            result = metaSection.getKey("refer")

            if (result.isNotEmpty())
            {
                prevLink = result
            }
            else
            {
                break
            }
        }
    }
}