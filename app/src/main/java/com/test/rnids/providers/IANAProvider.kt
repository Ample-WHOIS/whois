package com.test.rnids.providers

import android.content.Context
import com.test.rnids.WHOISClientWrapper
import com.test.rnids.parsers.BasicParser
import java.util.concurrent.locks.ReentrantLock

class IANAProvider(appContext: Context) : TLDProviderBase() {
    private val lock = ReentrantLock()
    private val condition = lock.newCondition()

    private val _appContext = appContext

    companion object {
        @JvmStatic lateinit var clientWrapper: WHOISClientWrapper
    }

    init {
        clientWrapper = WHOISClientWrapper(_appContext)
    }

    override fun getServer(domain: String) : String
    {
        if (domain.indexOf('.') == -1)
        {
            return "whois.iana.org"
        }

        var response = ""

        clientWrapper.query(domain) { resp ->
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
            return result
        }

        result = metaSection.getKey("whois")

        if (result.isNotEmpty())
        {
            return result
        }

        result = metaSection.getKey("refer")
        return result
    }
}