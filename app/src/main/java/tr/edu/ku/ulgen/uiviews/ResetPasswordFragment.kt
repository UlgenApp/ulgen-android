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

class ResetPasswordFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reset_password_screen, container, false)

        val resetPasswordButton = view.findViewById<Button>(R.id.btnBalantyGnder)

        val loginText = view.findViewById<TextView>(R.id.txtGiriyap)

        loginText.setOnClickListener {
            findNavController().navigate(R.id.action_resetPasswordFragment_to_loginScreenFragment)
        }

        resetPasswordButton.setOnClickListener {

        }


        return view
    }
}