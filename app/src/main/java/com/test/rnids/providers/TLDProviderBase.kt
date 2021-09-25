package com.test.rnids.providers

abstract class TLDProviderBase {
    abstract fun getServer(domain: String) : String
}