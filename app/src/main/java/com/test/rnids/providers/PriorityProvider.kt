package com.test.rnids.providers

import android.content.Context
import androidx.lifecycle.MutableLiveData
import org.json.JSONException
import org.json.JSONObject

class PriorityProvider(appContext: Context) : TLDProviderBase() {
    companion object {
        @JvmStatic var servList: JSONObject? = null
    }

    private val _appContext = appContext

    override fun getServer(domain: String) : String
    {
        if (servList == null)
        {
            try {
                val stream = _appContext.resources.openRawResource(com.test.rnids.R.raw.priority)
                val b = ByteArray(stream.available())
                stream.read(b)
                servList = JSONObject(String(b))
            } catch (e: Exception) { }
        }

        var str = domain.substringAfter('.')
        var result = ""
        while (true)
        {
            try {
                result = servList!![str] as String
                break
            } catch (e: JSONException)
            {
                val oldStr = str
                str = str.substringAfter('.')

                if (oldStr == str)
                {
                    break
                }
            }
        }

        return result
    }
}