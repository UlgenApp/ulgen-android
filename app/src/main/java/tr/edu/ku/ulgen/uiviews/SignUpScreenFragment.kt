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
import tr.edu.ku.ulgen.model.apibodies.UserBody
import tr.edu.ku.ulgen.model.apiinterface.ApiInterface
import tr.edu.ku.ulgen.model.datasource.AuthenticationDataSource
import tr.edu.ku.ulgen.uifeedbackmessage.CustomSnackbar

class SignUpScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_up_screen, container, false)

        val singUpButton = view.findViewById<Button>(R.id.btnHesapOlutur)

        singUpButton.setOnClickListener {
            val email = view.findViewById<EditText>(R.id.etEmailOne).text.toString()
            val fullName = view.findViewById<EditText>(R.id.etGroupNine).text.toString()
            val password = view.findViewById<EditText>(R.id.etPassword).text.toString()
            val (names, surname) = parseName(fullName)

            signUp(email,names, surname, password, view)

        }

        val loginTextView = view.findViewById<TextView>(R.id.txtZatenhesabnv)
        loginTextView.setOnClickListener {
            findNavController().navigate(R.id.action_signUpScreenFragment_to_loginScreenFragment)
        }

        return view
    }
    private fun signUp(email: String, name: String,
                       surname: String, password: String, view: View){
        val retIn = AuthenticationDataSource.getRetrofitInstance().create(ApiInterface::class.java)
        val registerInfo = UserBody(email,name, surname,password)

        retIn.registerUser(registerInfo).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    CustomSnackbar.showSignUp(view, getString(R.string.sign_up_succesfull))

                }
                else{
                    Log.d("SIGN_F", response.message().toString())
                    CustomSnackbar.showError(view, getString(R.string.sign_up_failed))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("SIGN_F",  "onFailure " + t.message)
                CustomSnackbar.showError(view, getString(R.string.sign_up_failed))
            }


        })
    }
    fun parseName(fullName: String): Pair<String, String> {
        val nameParts = fullName.split(" ")

        if (nameParts.size == 2) {
            // One name and one surname
            return Pair(nameParts[0], nameParts[1])
        } else if (nameParts.size > 2) {
            // More than one name and one surname
            val names = nameParts.subList(0, nameParts.size - 1).joinToString(" ")
            val surname = nameParts[nameParts.size - 1]
            return Pair(names, surname)
        } else {
            throw IllegalArgumentException("Invalid name format!")
        }
    }


}