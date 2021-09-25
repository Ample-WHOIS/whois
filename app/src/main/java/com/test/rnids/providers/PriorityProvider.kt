package com.test.rnids.providers

import android.content.Context
import androidx.lifecycle.MutableLiveData
import org.json.JSONException
import org.json.JSONObject

class PriorityProvider : TLDProvider() {
    companion object {
        @JvmStatic var servList: JSONObject? = null
    }

    override fun getServer(appContext: Context, domain: String) : MutableLiveData<String>
    {
        if (servList == null)
        {
            try {
                val stream = appContext.resources.openRawResource(com.test.rnids.R.raw.priority)
                val b = ByteArray(stream.available())
                stream.read(b)
                servList = JSONObject(String(b))
            } catch (e: Exception) { }
        }

        var str = domain.substringAfter('.')
        val result = MutableLiveData("")
        while (true)
        {
            try {
                result.value = servList!![str] as String
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