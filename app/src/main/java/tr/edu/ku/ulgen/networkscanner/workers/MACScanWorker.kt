package tr.edu.ku.ulgen.networkscanner.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import tr.edu.ku.ulgen.networkscanner.scanner.LocalMACScanner
import java.util.concurrent.TimeUnit

class MACScanWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        Log.d("UlgenMACScanWorker", "doWork started")
        val macAddresses = LocalMACScanner.getMacAddresses()
        for (elem in macAddresses) {
            println(elem.value.address.subSequence(0,8))
        }
        return Result.success()
    }

    companion object {
        private const val TAG = "UlgenMACScanWorker-7c9db77f"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresCharging(true)
                .build()

            val macScanRequest = PeriodicWorkRequestBuilder<MACScanWorker>(1, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.KEEP, macScanRequest)
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(TAG)
        }
    }
}

