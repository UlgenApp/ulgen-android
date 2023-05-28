package tr.edu.ku.ulgen.uiviews

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tr.edu.ku.ulgen.R
import tr.edu.ku.ulgen.model.SharedPreferencesUtil
import tr.edu.ku.ulgen.model.apibodies.AdditionalInfoBody
import tr.edu.ku.ulgen.model.apibodies.UserProfile
import tr.edu.ku.ulgen.model.datasource.UlgenAPIDataSource
import tr.edu.ku.ulgen.model.datasource.UserImageDataSource
import tr.edu.ku.ulgen.networkscanner.workers.MACScanWorker
import tr.edu.ku.ulgen.uifeedbackmessage.CustomSnackbar

class ProfileScreenFragment : Fragment() {
    private lateinit var name: TextView
    private lateinit var surname: TextView
    private lateinit var email: TextView
    private lateinit var additionalInfo: EditText
    private lateinit var logoutButton: ImageButton
    private lateinit var sharedPreferencesUtil: SharedPreferencesUtil
    private lateinit var profilePicture: CircleImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_screen, container, false)

        sharedPreferencesUtil = SharedPreferencesUtil(requireContext())

        name = view.findViewById(R.id.TextAd)
        surname = view.findViewById(R.id.TextSoyad)
        email = view.findViewById(R.id.emailText)
        additionalInfo = view.findViewById(R.id.additionalInfoText)
        logoutButton = view.findViewById(R.id.btnLogout)
        profilePicture = view.findViewById(R.id.imageProfilePicture)

        getUserProfileData()
        getUserImageData()

        logoutButton.setOnClickListener {
            logout()
        }


        val button = view.findViewById<Button>(R.id.btnBilgilerimiGncelle)

        button.setOnClickListener {
            val additionalInfoText = additionalInfo.text.toString()
            updateAdditionalInfo(additionalInfoText)
        }



        return view
    }

    private fun getUserProfileData() {
        val apiInterface = UlgenAPIDataSource.getUlgenAPIData()

        apiInterface.getUserProfile().enqueue(object : Callback<UserProfile> {
            override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                if (response.isSuccessful) {
                    val userProfile = response.body()


                    userProfile?.let {
                        sharedPreferencesUtil.saveUserProfile(it)
                    }

                    name.text = userProfile?.firstName
                    surname.text = userProfile?.lastName
                    email.text = userProfile?.email
                    additionalInfo.setText(userProfile?.additionalInfo)
                } else {
                    name.text = "N/A"
                    surname.text = "N/A"
                    email.text = "N/A"

                }
            }

            override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                name.text = "N/A"
                surname.text = "N/A"
                email.text = "N/A"
            }
        })
    }

    private fun getUserImageData() {
        val userImageDataSource = UserImageDataSource(requireContext())
        userImageDataSource.getUserImage().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val bitmap = BitmapFactory.decodeStream(response.body()?.byteStream())
                    profilePicture.setImageBitmap(bitmap)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }
        })
    }

    private fun updateAdditionalInfo(additionalInfoText: String) {
        val body = AdditionalInfoBody(additionalInfoText)

        val apiInterface = UlgenAPIDataSource.getUlgenAPIData()
        apiInterface.updateUserAdditionalInfo(body).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    CustomSnackbar.showInfo(view!!, "Bilgileriniz Kaydedildi.")

                } else {

                    CustomSnackbar.showError(view!!, "Bir hata oluştu.")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                CustomSnackbar.showError(view!!, "Bir hata oluştu.")
            }
        })
    }


    private fun logout() {
        sharedPreferencesUtil.clearAllData()
        MACScanWorker.cancel(requireContext())
        restartActivity()
    }

    private fun restartActivity() {
        val intent = requireActivity().intent
        requireActivity().finish()
        startActivity(intent)
    }


}
