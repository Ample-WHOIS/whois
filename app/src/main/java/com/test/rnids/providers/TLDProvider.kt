package com.test.rnids.providers

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.test.rnids.DepResolver
import com.test.rnids.util.isHostAvailable
import com.test.rnids.util.validateDomain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.locks.ReentrantLock
import kotlin.coroutines.EmptyCoroutineContext

class TLDProvider(context: Context) : TLDProviderBase() {
    val scope: CoroutineScope = CoroutineScope(EmptyCoroutineContext)

    private val lock = ReentrantLock()
    private val condition = lock.newCondition()

    private val _context = context

    override fun getServer(domain: String) : String
    {
        var provider: TLDProviderBase
        var result: String
        if (domain.indexOf('.') != -1)
        {
            provider = DepResolver.Resolve(PriorityProvider::class) as TLDProviderBase
            result = provider.getServer(domain)

            if (validateDomain(result))
            {
                return result
            }
        }

        var reachable = false
        scope.launch {
            isHostAvailable(_context,"whois.iana.org", 43, 2000) {
                reachable = it

                lock.lock()
                try {
                    condition.signalAll()
                } finally {
                    lock.unlock()
                }
            }
        }

        lock.lock()
        try {
            condition.await()
        } finally {
            lock.unlock()
        }

        if (reachable)
        {
            provider = DepResolver.Resolve(IANAProvider::class) as IANAProvider
            result = provider.getServer(domain)

            if (validateDomain(result))
            {
                return result
            }
        }
        else if (domain.indexOf('.') == -1)
        {
            return ""
        }

        provider = DepResolver.Resolve(FileProvider::class) as FileProvider
        result = provider.getServer(domain)

        if (validateDomain(result))
        {
            return result
        }

        return ""
    }
}