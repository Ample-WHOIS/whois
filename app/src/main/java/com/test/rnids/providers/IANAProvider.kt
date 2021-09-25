package com.test.rnids.providers

import androidx.lifecycle.MutableLiveData
import com.test.rnids.WHOISClientWrapper
import java.util.concurrent.locks.ReentrantLock

class IANAProvider : TLDProviderBase() {
    private val lock = ReentrantLock()
    private val condition = lock.newCondition()

    companion object {
        @JvmStatic lateinit var clientWrapper: WHOISClientWrapper
    }

    init {
        clientWrapper = WHOISClientWrapper()
    }

    override fun getServer(domain: String) : String
    {
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

        return response
    }
}