package com.test.rnids.parsers

import android.content.Context
import androidx.lifecycle.MutableLiveData
import org.json.JSONObject
import java.lang.Integer.max
import java.util.*

class BasicSection(_title: String = "Miscellaneous Information") : Observable() {
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
            if (map[key]!![map[key]!!.length - 1] != '\n')
            {
                map[key] = map[key] + "\n"
            }
            map[key] = map[key] + value.padStart(key.length + 2 + value.length) + "\n"
        }
        notifyObservers()
    }

    fun getKey(key: String) : String
    {
        if (map.containsKey(key))
        {
            return map[key]!!
        }
        return ""
    }

    fun isEmpty() : Boolean
    {
        return map.keys.isEmpty()
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

class BasicParser(appContext: Context) : Observable() {
    companion object {
        @JvmStatic var knownEntries: JSONObject? = null
        @JvmStatic var knownSections: JSONObject? = null
    }

    private val map : MutableMap<String, BasicSection> = mutableMapOf(
        Pair("misc", BasicSection())
    )

    private val _appContext = appContext
    private var _raw = ""

    fun getRaw() : String
    {
        return _raw
    }

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

        _raw = raw

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

        notifyObservers()
        for (key in map.keys)
        {
            map[key]!!.addObserver { _, _ -> notifyObservers() }
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

    fun getSectionsOrdered() : MutableList<BasicSection>
    {
        var basic: BasicSection? = null
        var dns: BasicSection? = null
        var meta: BasicSection? = null
        var misc: BasicSection? = null

        val res: MutableList<BasicSection> = mutableListOf()
        for (key in map.keys)
        {
            val value = map[key]!!
            when (key)
            {
                "basic" -> { basic = value }
                "dns" -> { dns = value }
                "meta" -> { meta = value }
                "misc" -> { misc = value }
                else -> { res.add(value) }
            }
        }

        if (basic != null)
        {
            res.add(0, basic)
        }

        if (dns != null)
        {
            res.add(res.size, dns)
        }

        if (meta != null)
        {
            res.add(res.size, meta)
        }

        if (misc != null)
        {
            res.add(res.size, misc)
        }

        return res
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
