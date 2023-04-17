package tr.edu.ku.ulgen.uiviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import tr.edu.ku.ulgen.R

class SignUpScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_sign_up_screen, container, false)

        val loginTextView = view.findViewById<TextView>(R.id.txtZatenhesabnv)
        loginTextView.setOnClickListener {
            findNavController().navigate(R.id.action_signUpScreenFragment_to_loginScreenFragment)
        }

        return view
    }
}