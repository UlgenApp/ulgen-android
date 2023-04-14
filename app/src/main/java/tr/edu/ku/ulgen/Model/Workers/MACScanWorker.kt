package tr.edu.ku.ulgen.Model.Workers

import android.content.Context
import android.util.Log
import androidx.work.*
import tr.edu.ku.ulgen.Model.Scanner.LocalMACScanner
import java.util.concurrent.TimeUnit

class MACScanWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        // Do the work here
        Log.d("MACScanWorker", "doWork started")
        val macAddresses = LocalMACScanner.getMacAddresses()
        for (elem in macAddresses) {
            println(elem.value.address.subSequence(0,8))
        }
        return Result.success()
    }

    companion object {
        private const val TAG = "MACScanWorker"

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

