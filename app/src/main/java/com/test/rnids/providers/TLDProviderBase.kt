package com.test.rnids.providers

import android.content.Context
import androidx.lifecycle.MutableLiveData

abstract class TLDProvider {
    abstract fun getServer(appContext: Context, domain: String) : MutableLiveData<String>
}