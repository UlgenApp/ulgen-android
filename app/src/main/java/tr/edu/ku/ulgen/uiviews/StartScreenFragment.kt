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

class StartScreenFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_start_screen, container, false)

        val createAccountButton = view.findViewById<Button>(R.id.btnHesapOlutur)
        val loginTextView = view.findViewById<TextView>(R.id.txtZatenhesabnv)
        createAccountButton.setOnClickListener {
            findNavController().navigate(R.id.action_startScreenFragment_to_signUpScreenFragment)
        }
        loginTextView.setOnClickListener {
            findNavController().navigate(R.id.action_startScreenFragment_to_loginScreenFragment)
        }




        return view
    }
}