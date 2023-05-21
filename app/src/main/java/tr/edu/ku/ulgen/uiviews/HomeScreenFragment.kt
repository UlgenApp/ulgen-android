package tr.edu.ku.ulgen.uiviews

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar

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

class HomeScreenFragment : Fragment() {

    private lateinit var sharedPreferencesUtil: SharedPreferencesUtil
    private lateinit var userImageView: CircleImageView


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
            findNavController().navigate(R.id.action_homeScreenFragment_to_userProfileScreenFragment)
        }




        val nameText = view.findViewById<TextView>(R.id.txtLanguage)

        nameText.text = "Merhaba " + sharedPreferencesUtil.getUserProfile()?.firstName.toString() + "!"

        /*val logoutImageButton = view.findViewById<ImageView>(R.id.logoutButton)
        logoutImageButton.setOnClickListener {
            logout()
        }*/

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
