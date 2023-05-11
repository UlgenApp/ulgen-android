package tr.edu.ku.ulgen.uiviews

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
import tr.edu.ku.ulgen.uifeedbackmessage.CustomSnackbar


class LoginScreenFragment : Fragment() {

    private lateinit var sharedPreferencesUtil: SharedPreferencesUtil



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_login_screen, container, false)
        sharedPreferencesUtil = SharedPreferencesUtil(requireContext())




        val resetPasswordTextView = view.findViewById<TextView>(R.id.txtIfrenimiunut)
        resetPasswordTextView.setOnClickListener {
            findNavController().navigate(R.id.action_loginScreenFragment_to_resetPasswordFragment)
        }

        val loginButton = view.findViewById<Button>(R.id.btnGiriYap)
        loginButton.setOnClickListener {

            val email = view.findViewById<EditText>(R.id.etEmailOne).text.toString()
            val password = view.findViewById<EditText>(R.id.etPassword).text.toString()
            signIn(email, password, view)
        }

        return view
    }
    private fun signIn(email: String, password: String, view: View){
        val retIn = AuthenticationDataSource.getRetrofitInstance().create(ApiInterface::class.java)
        val signInInfo = SignInBody(email, password)
        retIn.signin(signInInfo).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    val responseBody = response.body()
                    responseBody?.let {
                        try {
                            val responseBodyString = it.string()
                            val json = JSONObject(responseBodyString)
                            val apiToken = json.getString("token")
                            sharedPreferencesUtil.saveApiToken(apiToken)
                            Log.d("Shared_preferences", sharedPreferencesUtil.getApiToken().toString())
                            getUserProfile(apiToken)
                            findNavController().navigate(R.id.action_loginScreenFragment_to_homeScreenFragment)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    Log.d("LOGIN_F", response.message().toString())
                    CustomSnackbar.showError(view, getString(R.string.login_failed))

                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("LOGIN_F",  "onFailure " + t.message)
                CustomSnackbar.showError(view, getString(R.string.login_failed))

            }

        })
    }
    private fun getUserProfile(token: String) {
        val retIn = AuthenticationDataSource.getRetrofitInstance().create(ApiInterface::class.java)
        retIn.getUserProfile("Bearer $token").enqueue(object : Callback<UserProfile> {
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
                        Log.d("Shared_preferences", sharedPreferencesUtil.getUserProfile().toString())
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