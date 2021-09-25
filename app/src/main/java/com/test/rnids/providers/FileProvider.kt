package com.test.rnids.providers

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.test.rnids.util.validateDomain

class FileProvider : TLDProvider() {
    companion object {
        @JvmStatic var servList: String? = null
    }

    override fun getServer(appContext: Context, domain: String) : MutableLiveData<String>
    {
        if (servList == null)
        {
            try {
                val stream = appContext.resources.openRawResource(com.test.rnids.R.raw.tld_serv_list)
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
            if (domain.indexOf(match.groupValues[0]) != -1)
            {
                if (bestMatch == null ||
                    match.groupValues[0].count { it == '.' } >
                    bestMatch.groupValues[0].count { it == '.' })
                {
                    bestMatch = match
                }
            }
        }

        val result = MutableLiveData(bestMatch!!.groupValues[1])
        if (result.value!!.startsWith("NONE") || result.value!!.startsWith("WEB"))
        {
            result.value = ""
        }

        validateDomain(result)
        return result
    }
}