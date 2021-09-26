package com.test.rnids.ui.history

import com.test.rnids.util.tryParseDate

class HistoryEntryModel(domain: String, ts: String, exp: String) {
    val domainName = domain
    var timestamp: Long = -1
    var expiry: Long = -1

    init {
        var tmp = tryParseDate(ts)
        if (tmp != null)
        {
            timestamp = tmp.time
        }

        tmp = tryParseDate(exp)
        if (tmp != null)
        {
            expiry = tmp.time
        }
    }
}
