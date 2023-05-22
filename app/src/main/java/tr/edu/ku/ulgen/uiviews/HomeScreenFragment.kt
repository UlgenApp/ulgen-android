package tr.edu.ku.ulgen.uiviews

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tr.edu.ku.ulgen.R
import tr.edu.ku.ulgen.model.SharedPreferencesUtil
import tr.edu.ku.ulgen.model.datasource.UserImageDataSource
import tr.edu.ku.ulgen.networkscanner.scanner.LocalMACScanner
import tr.edu.ku.ulgen.networkscanner.workers.MACScanWorker
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStates
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.common.api.ResolvableApiException

class HomeScreenFragment : Fragment() {

    private lateinit var sharedPreferencesUtil: SharedPreferencesUtil
    private lateinit var userImageView: CircleImageView

    @RequiresApi(Build.VERSION_CODES.Q)
    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions: Map<String, Boolean> ->
        val foregroundGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val backgroundGranted = permissions[Manifest.permission.ACCESS_BACKGROUND_LOCATION] ?: false

        if (foregroundGranted && !backgroundGranted) {
            val macAddresses = LocalMACScanner.getMacAddresses().values.map { it.address }
            LocalMACScanner.sendMACAddresses(macAddresses.toMutableList(), requireContext())
        } else if (foregroundGranted && backgroundGranted) {
            checkLocationSettingsAndTurnOn()
        } else {
            // Permission denied

        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestLocationPermissions() {
        val foregroundGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val backgroundGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!foregroundGranted || !backgroundGranted) {
            requestPermissionsLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            )
        } else {
            checkLocationSettingsAndTurnOn()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkLocationSettingsAndTurnOn() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(requireActivity())
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { _ ->
            // All location settings are satisfied. The client can initialize location requests here.
            if(SharedPreferencesUtil(requireContext()).getUserSafetyStatus()?.isSafe == false){
                MACScanWorker.schedule(requireContext())
            }
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                    exception.startResolutionForResult(requireActivity(), 1234)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_screen, container, false)

        sharedPreferencesUtil = SharedPreferencesUtil(requireContext())

        userImageView = view.findViewById(R.id.imageBlankprofilep)

        fetchUserImage()

        val toolbar = view.findViewById<Toolbar>(R.id.toolbarToolbar)
        toolbar.setOnClickListener {
            findNavController().navigate(R.id.action_homeScreenFragment_to_profileScreenFragment)
        }


        val nameText = view.findViewById<TextView>(R.id.txtLanguage)

        nameText.text =
            "Merhaba " + sharedPreferencesUtil.getUserProfile()?.firstName.toString() + "!"

        /*val logoutImageButton = view.findViewById<ImageView>(R.id.logoutButton)
        logoutImageButton.setOnClickListener {
            logout()
        }*/

        requestLocationPermissions()

        return view
    }

    private fun fetchUserImage() {
        val userImageDataSource = UserImageDataSource(requireContext())
        val userImageUrlCall = userImageDataSource.getUserImage()

        userImageUrlCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val imageUrl = response.raw().request.url.toString()
                    displayUserImage(imageUrl)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("HomeScreenFragment", "Failed to fetch user image", t)
            }
        })
    }

    private fun displayUserImage(imageUrl: String) {
        Glide.with(requireContext()).load(imageUrl).into(userImageView)
    }


    /*private fun logout() {
        // Clear user data from SharedPreferences
        sharedPreferencesUtil.clearAllData()

        // Navigate back to the login screen
        findNavController().navigate(R.id.action_homeScreenFragment_to_startScreenFragment)
    }*/
}
