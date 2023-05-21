package tr.edu.ku.ulgen.uiviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import tr.edu.ku.ulgen.R
import tr.edu.ku.ulgen.model.SharedPreferencesUtil

class HomeScreenFragment : Fragment() {

    private lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_screen, container, false)

        sharedPreferencesUtil = SharedPreferencesUtil(requireContext())

        val nameText = view.findViewById<TextView>(R.id.txtLanguage)

        nameText.text = "Merhaba " + sharedPreferencesUtil.getUserProfile()?.firstName.toString()

        val logoutImageButton = view.findViewById<ImageView>(R.id.logoutButton)
        logoutImageButton.setOnClickListener {
            logout()
        }

        return view
    }

    private fun logout() {
        // Clear user data from SharedPreferences
        sharedPreferencesUtil.clearAllData()

        // Navigate back to the login screen
        findNavController().navigate(R.id.action_homeScreenFragment_to_startScreenFragment)
    }
}
