package com.test.rnids.parsers

import android.content.Context
import org.json.JSONObject

class BasicSection(_title: String = "Miscellaneous Information") {
    var title: String = _title
    private val map : MutableMap<String, String> = mutableMapOf()

    fun addKey(key: String, value: String)
    {
        if (!map.containsKey(key))
        {
            map[key] = value
        }
        else
        {
            map[key] = map[key] + "\n" + value
        }
    }

    fun getKey(key: String) : String
    {
        if (map.containsKey(key))
        {
            return map[key]!!
        }
        return ""
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (key in map.keys)
        {
            sb.appendLine(key + ": " + map[key])
        }

        return sb.toString()
    }
}

class BasicParser(appContext: Context) {
    companion object {
        @JvmStatic var knownEntries: JSONObject? = null
        @JvmStatic var knownSections: JSONObject? = null
    }

    private val map : MutableMap<String, BasicSection> = mutableMapOf(
        Pair("misc", BasicSection())
    )

    private val _appContext = appContext

    fun processRaw(raw: String)
    {
        if (knownEntries == null)
        {
            try {
                val stream = _appContext.resources.openRawResource(com.test.rnids.R.raw.knownentries)
                val b = ByteArray(stream.available())
                stream.read(b)
                knownEntries = JSONObject(String(b))
            } catch (e: Exception) {
                return
            }
        }

        if (knownSections == null)
        {
            try {
                val stream = _appContext.resources.openRawResource(com.test.rnids.R.raw.knownsections)
                val b = ByteArray(stream.available())
                stream.read(b)
                knownSections = JSONObject(String(b))
            } catch (e: Exception) {
                return
            }
        }

        if (raw.isEmpty())
        {
            return
        }

        var currSection: String = ""

        val clearKeyValueRegex = "^[^:].*[^:][a-zA-Z ]:[a-zA-Z ].*".toRegex()
        for (line in raw.lines())
        {
            if (line.startsWith('%') || line.isBlank())
            {
                currSection = ""
                continue
            }

            val spec = checkSpecial(line)
            val clearKeyValue = clearKeyValueRegex.containsMatchIn(line)
            if (spec != null || !clearKeyValue || line.length > 256)
            {
                var valToUse = line
                if (clearKeyValue)
                {
                    valToUse = valToUse.substringAfter(':')
                                .trim().replace("[><]".toRegex(), "")
                }

                if (spec == null)
                {
                    getSection("misc")!!.addKey("Text Field", valToUse)
                }
                else
                {
                    getSection(spec.first)!!.addKey(spec.second, valToUse)
                }

                continue
            }

            val key = line.substringBefore(':')
                .trim()
                .replace("[- _]".toRegex(), "")
                .lowercase()

            val value = line.substringAfter(':').trim()

            val obj: JSONObject? = knownEntries!!.optJSONObject(key)
            if (obj == null || obj.optBoolean("variable"))
            {
                var sectionToUse = currSection
                if (sectionToUse.isEmpty())
                {
                    sectionToUse = "misc"
                }

                getSection(sectionToUse)!!.addKey(line.substringBefore(':'), value)
                continue
            }
            else
            {
                currSection = obj["section"] as String
                getSection(currSection)!!.addKey(line.substringBefore(':'), value)
            }
        }
    }

    private fun checkSpecial(str: String) : Pair<String, String>?
    {
        val adj = str.lowercase()
        if (adj.indexOf("complaint") != -1)
        {
            return Pair("misc", "Complaint Form")
        }

        if (adj.indexOf("last update") != -1 ||
            adj.indexOf("timestamp") != -1)
        {
            return Pair("meta", "Timestamp")
        }

        if (adj.indexOf("registrar whois") != -1 ||
            adj.indexOf("whois server") != -1)
        {
            return Pair("meta", "Downstream WHOIS")
        }

        return null
    }

    fun getSection(key: String) : BasicSection?
    {
        if (!map.containsKey(key))
        {
            val obj: JSONObject = knownSections!!.optJSONObject(key) ?: return map["misc"]
            map[key] = BasicSection(obj.optString("en", "Miscellaneous Information"))
        }

        return map[key]!!
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (key in map.keys)
        {
            val obj: JSONObject? = knownSections!!.optJSONObject(key)

            if (obj != null)
            {
                sb.appendLine(obj.optString("en", "Miscellaneous Information"))
                    .appendLine()
            }

            sb.append(map[key].toString())
        }

        return sb.toString()
    }
}
