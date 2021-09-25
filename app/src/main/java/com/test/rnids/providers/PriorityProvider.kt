package com.test.rnids.providers

import android.content.Context
import org.json.JSONException
import org.json.JSONObject

class PriorityProvider : TLDProvider() {
    companion object {
        @JvmStatic var servList: JSONObject? = null
    }

    override fun getServer(appContext: Context, domain: String) : String
    {
        if (FileProvider.servList == null)
        {
            try {
                val stream = appContext.resources.openRawResource(com.test.rnids.R.raw.priority)
                val b = ByteArray(stream.available())
                stream.read(b)
                servList = JSONObject(String(b))
            } catch (e: Exception) { }
        }

        var str = domain.substringAfter('.')
        var result = ""
        while (str.length > 0)
        {
            try {
                result = servList!![str] as String
                break
            } catch (e: JSONException)
            {
                str = str.substringAfter('.')
            }
        }

        return result
    }
}