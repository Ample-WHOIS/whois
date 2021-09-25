package com.test.rnids.providers

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.test.rnids.util.validateDomain

class FileProvider(appContext: Context) : TLDProviderBase() {
    companion object {
        @JvmStatic var servList: String? = null
    }

    private val _appContext = appContext

    override fun getServer(domain: String) : String
    {
        if (servList == null)
        {
            try {
                val stream = _appContext.resources.openRawResource(com.test.rnids.R.raw.tld_serv_list)
                val b = ByteArray(stream.available())
                stream.read(b)
                servList = String(b)
            } catch (e: Exception) { }
        }

        val TLD = domain.substringAfterLast('.')

        val regex = Regex("^((?:\\.[\\w-]+)*\\.${TLD})\\s+([^#\\t\\n]+).*\$",
            RegexOption.MULTILINE)
        val matches = regex.findAll(servList!!)

        var bestMatch : MatchResult? = null
        for (match in matches)
        {
            if (!match.groups.isEmpty() && domain.indexOf(match.groups[1]!!.value) != -1)
            {
                if (bestMatch == null ||
                    match.groups[1]!!.value.count { it == '.' } >
                    bestMatch.groups[1]!!.value.count { it == '.' })
                {
                    bestMatch = match
                }
            }
        }

        var result = ""
        if (bestMatch != null)
        {
            result = bestMatch.groups[2]!!.value
            if (result.startsWith("NONE") || result.startsWith("WEB"))
            {
                result = ""
            }
        }

        if (result.isNotEmpty())
        {
            for (thing in result.split(' '))
            {
                if (validateDomain(thing))
                {
                    result = thing
                    break
                }
            }
        }

        return result
    }
}