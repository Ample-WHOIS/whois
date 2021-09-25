package com.test.rnids.providers

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.test.rnids.WHOISClient
import com.test.rnids.util.await
import com.test.rnids.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import java.util.concurrent.locks.ReentrantLock

class IANAProvider : TLDProvider() {
    private val lock = ReentrantLock()
    private val condition = lock.newCondition()

    companion object {
        @JvmStatic lateinit var client: WHOISClient
    }

    init {
        client = WHOISClient()
    }

    override fun getServer(appContext: Context, domain: String) : MutableLiveData<String>
    {
        var response = ""

        client.query(MutableLiveData(domain), null) { resp ->
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

        return MutableLiveData(response)
    }
}