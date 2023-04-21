package tr.edu.ku.ulgen.uiviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import tr.edu.ku.ulgen.R

class LoginScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_login_screen, container, false)

        val resetPasswordTextView = view.findViewById<TextView>(R.id.txtIfrenimiunut)
        resetPasswordTextView.setOnClickListener {
            findNavController().navigate(R.id.action_loginScreenFragment_to_resetPasswordFragment)
        }

        val loginButton = view.findViewById<Button>(R.id.btnGiriYap)
        loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginScreenFragment_to_kandilliTableFragment)
        }

        return view
    }
}