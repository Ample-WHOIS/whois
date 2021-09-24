package com.test.rnids

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import org.apache.commons.net.whois.WhoisClient
import java.io.IOException

class WHOISWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {

    companion object {
        @JvmStatic lateinit var client: WhoisClient
    }

    init {
        client = WhoisClient()
    }

    override fun doWork(): Result {
        var result: String = ""

        try {
            client.connect("whois.iana.org")
            result = client.query("rnids.rs")
            client.disconnect()
        } catch (e: IOException) {
            result = e.message!!
        }

        val output: Data = workDataOf("result" to result)

        return Result.success(output)
    }
}