package tr.edu.ku.ulgen.networkscanner.scanner


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tr.edu.ku.ulgen.model.datasource.UlgenAPIDataSource
import tr.edu.ku.ulgen.model.macscannerdatastructure.Location
import tr.edu.ku.ulgen.model.macscannerdatastructure.MACScannerRequest
import tr.edu.ku.ulgen.model.macscannerdatastructure.MACScannerResponse
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.*
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import androidx.core.app.NotificationCompat
import tr.edu.ku.ulgen.R


object LocalMACScanner {

    @SuppressLint("StaticFieldLeak")
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private fun intToMacAddress(value: ByteArray) =
        MacAddress(value.joinToString(":") { String.format("%02X", it) })

    fun getMacAddresses(): Map<Inet4Address, MacAddress> {
        return try {
            NetworkInterface.getNetworkInterfaces()
                .toList()
                .flatMap { nic ->
                    nic.interfaceAddresses
                        .mapNotNull {
                            if (it.address is Inet4Address && nic.hardwareAddress != null) {
                                (it.address as Inet4Address) to intToMacAddress(nic.hardwareAddress)
                            } else {
                                null
                            }
                        }
                }
                .associate { it }
        } catch (ex: Exception) {
            ex.printStackTrace()
            emptyMap()
        }
    }

    fun sendMACAddresses(
        macAddressList: MutableList<String>,
        applicationContext: Context
    ): Boolean {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("UlgenMACScanWorker", "Location permission not granted")
            return false
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { androidLocation: android.location.Location? ->
            androidLocation?.let { location ->
                val geocoder = Geocoder(applicationContext, Locale.ENGLISH)
                val addresses: List<Address> =
                    geocoder.getFromLocation(location.latitude, location.longitude, 1)
                val cityName = addresses[0].adminArea
                if (cityName != null) {
                    val requestBody = MACScannerRequest(
                        location = Location(
                            latitude = location.latitude,
                            longitude = location.longitude
                        ),
                        macAddresses = macAddressList,
                        userCity = convertTurkishToEnglish(cityName)
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val macScannerData = UlgenAPIDataSource.getUlgenAPIData()
                            macScannerData.sendMACAddresses(requestBody).enqueue(object :
                                Callback<MACScannerResponse> {
                                override fun onResponse(
                                    call: Call<MACScannerResponse>,
                                    response: Response<MACScannerResponse>
                                ) {
                                    if (response.isSuccessful) {
                                        println("MAC addresses sent successfully")
                                    }
                                    else if(response.code() == 403) {
                                        showNotification(applicationContext, "Oturumunun süresi doldu", "Lütfen uygulamayı kullanmak için giriş yap", 403)
                                    }
                                    else {
                                        println("Response failed with status code: ${response.code()}")
                                        println("Body: ${response.body()}")
                                    }
                                }

                                override fun onFailure(
                                    call: Call<MACScannerResponse>,
                                    t: Throwable
                                ) {
                                    println("Failed to get data: ${t.message}")
                                }
                            })
                        } catch (e: Exception) {

                            println(e)
                        }
                    }
                } else {
                    Log.e("UlgenMACScanWorker", "Could not get city name from Geocoder")
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("UlgenMACScanWorker", "Failed to get last location", exception)
        }
        return true
    }

    fun convertTurkishToEnglish(text: String): String {
        val turkishChars = charArrayOf('ş', 'ğ', 'ı', 'ö', 'ü', 'ç', 'Ş', 'Ğ', 'İ', 'Ö', 'Ü', 'Ç')
        val englishChars = charArrayOf('s', 'g', 'i', 'o', 'u', 'c', 'S', 'G', 'I', 'O', 'U', 'C')

        var result = text
        for (i in turkishChars.indices) {
            result = result.replace(turkishChars[i], englishChars[i])
        }
        return result
    }

    fun showNotification(context: Context, title: String, message: String, notificationId: Int) {
        val channelId = "UlgenMACScanner_channel_01"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, "Ulgen MACScanner Notification", importance)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)  // replace with your own icon
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    data class MacAddress(val address: String)
}
