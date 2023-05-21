package tr.edu.ku.ulgen.uiviews

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tr.edu.ku.ulgen.R
import tr.edu.ku.ulgen.model.SharedPreferencesUtil
import tr.edu.ku.ulgen.model.apibodies.SignInBody
import tr.edu.ku.ulgen.model.apiinterface.ApiInterface
import tr.edu.ku.ulgen.model.datasource.AuthenticationDataSource
import org.json.JSONObject
import org.json.JSONException
import tr.edu.ku.ulgen.model.apibodies.UserProfile
import tr.edu.ku.ulgen.model.datasource.UlgenAPIDataSource
import tr.edu.ku.ulgen.networkscanner.scanner.LocalMACScanner
import tr.edu.ku.ulgen.networkscanner.workers.MACScanWorker
import tr.edu.ku.ulgen.uifeedbackmessage.CustomSnackbar


class LoginScreenFragment : Fragment() {

    private lateinit var sharedPreferencesUtil: SharedPreferencesUtil
    private lateinit var loadingFrame: FrameLayout

    @RequiresApi(Build.VERSION_CODES.Q)
    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions: Map<String, Boolean> ->
        val foregroundGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val backgroundGranted = permissions[Manifest.permission.ACCESS_BACKGROUND_LOCATION] ?: false

        if (foregroundGranted && !backgroundGranted) {
            // Only Foreground location permission was granted
            // Check if both permissions are granted and send MAC addresses if necessary
            val macAddresses = LocalMACScanner.getMacAddresses().values.map { it.address }
            LocalMACScanner.sendMACAddresses(macAddresses.toMutableList(), requireContext())
        } else if (foregroundGranted && backgroundGranted) {
            // Background location permission was granted
            MACScanWorker.schedule(requireContext())
        } else {
            // Permission denied
            // Show the user a message about the importance of this permission
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login_screen, container, false)
        sharedPreferencesUtil = SharedPreferencesUtil(requireContext())

        loadingFrame = view.findViewById(R.id.loading_frame)

        val resetPasswordTextView = view.findViewById<TextView>(R.id.txtIfrenimiunut)
        resetPasswordTextView.setOnClickListener {
            findNavController().navigate(R.id.action_loginScreenFragment_to_resetPasswordFragment)
        }

        val loginButton = view.findViewById<Button>(R.id.btnGiriYap)
        loginButton.setOnClickListener {

            val email = view.findViewById<EditText>(R.id.etEmailOne).text.toString()
            val password = view.findViewById<EditText>(R.id.etPassword).text.toString()
            showLoading(true)
            signIn(email, password, view)
        }

        return view
    }

    private fun showLoading(isLoading: Boolean) {
        loadingFrame.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun signIn(email: String, password: String, view: View) {
        val retIn = UlgenAPIDataSource.getUlgenAPIData()
        val signInInfo = SignInBody(email, password)
        retIn.signin(signInInfo).enqueue(object : Callback<ResponseBody> {
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    val responseBody = response.body()
                    responseBody?.let {
                        try {
                            val responseBodyString = it.string()
                            val json = JSONObject(responseBodyString)
                            val apiToken = json.getString("token")
                            sharedPreferencesUtil.saveApiToken(apiToken)
                            Log.d(
                                "Shared_preferences",
                                sharedPreferencesUtil.getApiToken().toString()
                            )
                            requestLocationPermissions()
                            getUserProfile()
                            findNavController().navigate(R.id.action_loginScreenFragment_to_homeScreenFragment)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    Log.d("LOGIN_F", response.message().toString())
                    CustomSnackbar.showError(view, getString(R.string.login_failed))

                }
                showLoading(false)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("LOGIN_F", "onFailure " + t.message)
                CustomSnackbar.showError(view, getString(R.string.login_failed))
                showLoading(false)
            }

        })
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
            // Permissions already granted
            MACScanWorker.schedule(requireContext())
        }
    }

    private fun getUserProfile() {
        val retIn = UlgenAPIDataSource.getUlgenAPIData()
        retIn.getUserProfile().enqueue(object : Callback<UserProfile> {
            override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                if (response.isSuccessful) {
                    val userProfile = response.body()
                    Log.d("GET_PROFILE_S", userProfile.toString())
                    userProfile?.let {
                        val firstName = it.firstName
                        val lastName = it.lastName
                        val email = it.email
                        val additionalInfo = it.additionalInfo

                        val sharedPreferencesUtil = SharedPreferencesUtil(requireContext())
                        sharedPreferencesUtil.saveUserProfile(userProfile)
                        Log.d(
                            "Shared_preferences",
                            sharedPreferencesUtil.getUserProfile().toString()
                        )
                    }
                } else {
                    // Handle unsuccessful response
                    Log.d("GET_PROFILE_F", response.message().toString())
                }
            }

            override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                // Handle failure
                Log.d("GET_PROFILE", "onFailure: ${t.message}")
            }
        })
    }


}