package com.test.rnids

import android.content.Context
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.work.*
import org.apache.commons.net.whois.WhoisClient
import java.io.IOException
import androidx.work.NetworkType
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class WHOISWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {

    companion object {
        @JvmStatic lateinit var client: WhoisClient
    }

    init {
        client = WhoisClient()
    }

    override fun doWork(): Result {
        var result = ""

        try {
            client.connect(inputData.getString("server"))
            result = client.query(inputData.getString("domain"))
            client.disconnect()
        } catch (e: IOException) {
            result = e.message!!
        }

        val output: Data = workDataOf("result" to result)

        return Result.success(output)
    }
}

fun <T> LiveData<T>.observeOnce(observer: Observer<T>) {
    observeForever(object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)

    afterObserve.invoke()

    // Don't wait indefinitely if the LiveData is not set.
    if (!latch.await(time, timeUnit)) {
        this.removeObserver(observer)
        throw TimeoutException("LiveData value was never set.")
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}

class WHOISModel {
    val timestamp = MutableLiveData<Long>()
    val result = MutableLiveData<String>()

    fun query(appContext: Context, domain: String, server: String = "whois.iana.org")
    {
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED)
        val data = Data.Builder().putString("domain", domain).putString("server", server)

        val WHOISWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<WHOISWorker>()
            .setInputData(data.build())
            .setConstraints(constraints.build())
            .build()
        WorkManager.getInstance(appContext).enqueue(WHOISWorkRequest)

        WorkManager.getInstance(appContext).getWorkInfoByIdLiveData(WHOISWorkRequest.id)
            .observeOnce { info ->
                if (info != null && info.state.isFinished) {
                    timestamp.value = System.currentTimeMillis() / 1000
                    result.value = info.outputData.getString("result")!!
                }
            }
    }
}