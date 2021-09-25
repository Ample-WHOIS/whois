package com.test.rnids

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.test.rnids.providers.FileProvider
import com.test.rnids.providers.IANAProvider
import com.test.rnids.providers.PriorityProvider
import com.test.rnids.providers.TLDProviderBase
import com.test.rnids.util.validateDomain

object TLDResolver {
    fun getServer(appContext: Context, domain: String) : MutableLiveData<String>
    {
        var provider: TLDProviderBase = DepResolver.Resolve(PriorityProvider::class) as TLDProviderBase
        var result = provider.getServer(appContext, domain)
        validateDomain(result)

        if (result.value!!.isNotEmpty())
        {
            return result
        }

        provider = DepResolver.Resolve(IANAProvider::class) as IANAProvider
        result = provider.getServer(appContext, domain)
        validateDomain(result)

        if (result.value!!.isNotEmpty())
        {
            return result
        }

        provider = DepResolver.Resolve(FileProvider::class) as FileProvider
        result = provider.getServer(appContext, domain)
        validateDomain(result)

        return result
    }
}