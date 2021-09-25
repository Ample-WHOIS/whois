package com.test.rnids.providers

import android.content.Context
import com.test.rnids.WHOISModel
import com.test.rnids.getOrAwaitValue

class IANAProvider : TLDProvider() {
    companion object {
        @JvmStatic lateinit var model: WHOISModel
    }

    init {
        model = WHOISModel()
    }

    override fun getServer(appContext: Context, domain: String) : String
    {
        model.query(appContext, domain)
        return model.result.getOrAwaitValue { }
    }
}