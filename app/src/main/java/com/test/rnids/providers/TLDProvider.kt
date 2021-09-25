package com.test.rnids.providers

import android.content.Context

abstract class TLDProvider {
    abstract fun getServer(appContext: Context, domain: String) : String
}