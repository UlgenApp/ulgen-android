package tr.edu.ku.ulgen.networkscanner.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.heatmaps.WeightedLatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tr.edu.ku.ulgen.model.datasource.UlgenAPIDataSource
import tr.edu.ku.ulgen.model.heatmapdatastructure.HeatMapResponse
import tr.edu.ku.ulgen.model.macscannerdatastructure.Location
import tr.edu.ku.ulgen.model.macscannerdatastructure.MACScannerRequest
import tr.edu.ku.ulgen.model.macscannerdatastructure.MACScannerResponse
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

        val requestBody = MACScannerRequest(
            location = Location(latitude = 32.560431, longitude = 36.394458),
            macAddresses = macAddressList,
            userCity = "Istanbul"
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val macScannerData = UlgenAPIDataSource.getUlgenAPIData()
                macScannerData.sendMACAddresses(requestBody).enqueue(object: Callback<MACScannerResponse> {
                    override fun onResponse(call: Call<MACScannerResponse>, response: Response<MACScannerResponse>) {
                        if(response.isSuccessful) {
                            println("MAC addresses sent successfully")
                        } else {
                            println("Response failed with status code: ${response.code()}")
                            println("Body: ${response.body()}")
                        }
                    }

                    override fun onFailure(call: Call<MACScannerResponse>, t: Throwable) {
                        println("Failed to get data: ${t.message}")
                    }
                })
            } catch (e: Exception) {
                // TODO: Handle the error
                println(e)
            }
        }

        return Result.success()
    }


    companion object {
        private const val TAG = "UlgenMACScanWorker-7c9db77ft"

        fun schedule(context: Context) {
            UlgenAPIDataSource.init(context)
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresCharging(true)
                .build()

            val macScanRequest = PeriodicWorkRequestBuilder<MACScanWorker>(1, TimeUnit.MINUTES)
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

