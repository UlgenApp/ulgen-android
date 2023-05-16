package tr.edu.ku.ulgen.uiviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
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

        nameText.text = sharedPreferencesUtil.getUserProfile()?.firstName.toString()



        return view
    }
}