package com.test.rnids.providers

import android.content.Context

class FileProvider : TLDProvider() {
    companion object {
        @JvmStatic var servList: String? = null
    }

    override fun getServer(appContext: Context, domain: String) : String
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
        var matches = regex.findAll(servList!!)

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

        var result = bestMatch!!.groupValues[1]
        if (result.startsWith("NONE") || result.startsWith("WEB"))
        {
            result = ""
        }

        val validationRegex =
            Regex("((?!-))(xn--)?[a-z0-9][a-z0-9-_]{0,61}[a-z0-9]?\\.(xn--)?([a-z0-9\\-]{1,61}|[a-z0-9-]{1,30}\\.[a-z]{2,})")
        matches = validationRegex.findAll(result)

        var count = 0
        for (match in matches)
        {
            result = match.value
            if (++count > 1)
            {
                break
            }
        }

        if (count != 1)
        {
            result = ""
        }

        return result
    }
}