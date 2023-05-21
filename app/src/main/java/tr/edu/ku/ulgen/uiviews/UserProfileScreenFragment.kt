package tr.edu.ku.ulgen.uiviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import tr.edu.ku.ulgen.R
import tr.edu.ku.ulgen.model.SharedPreferencesUtil

class UserProfileScreenFragment : Fragment() {

    private lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_profile_screen, container, false)

        sharedPreferencesUtil = SharedPreferencesUtil(requireContext())


        val logoutButton = view.findViewById<Button>(R.id.btnLogout)



        logoutButton.setOnClickListener {
            logout()
        }
        return view
    }

    private fun logout() {
        sharedPreferencesUtil.clearAllData()
        findNavController().navigate(R.id.action_userProfileScreenFragment_to_startScreenFragment)
    }
}