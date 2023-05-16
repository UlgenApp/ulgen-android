package tr.edu.ku.ulgen.networkscanner.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import tr.edu.ku.ulgen.model.datasource.UlgenAPIDataSource
import tr.edu.ku.ulgen.networkscanner.scanner.LocalMACScanner
import java.util.concurrent.TimeUnit

class MACScanWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        Log.d("UlgenMACScanWorker", "doWork started")

        val macAddresses = LocalMACScanner.getMacAddresses()
        val macAddressList = mutableListOf<String>()
        for (elem in macAddresses) {
            println(elem)
            macAddressList.add(elem.value.address.subSequence(0,8).toString())
        }

        return if (LocalMACScanner.sendMACAddresses(macAddressList, applicationContext)){
            Result.success()
        } else {
            Result.failure()
        }
    }
    companion object {
        private const val TAG = "UlgenMACScanWorker-7c9db77fp"

        fun schedule(context: Context) {
            UlgenAPIDataSource.init(context)
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

