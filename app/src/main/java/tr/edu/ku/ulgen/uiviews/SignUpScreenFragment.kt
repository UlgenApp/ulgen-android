package tr.edu.ku.ulgen.uiviews

import android.app.AlertDialog
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
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

        val signUpButton = view.findViewById<Button>(R.id.btnHesapOlutur)

        signUpButton.setOnClickListener {
            if(view.findViewById<CheckBox>(R.id.viewRectangleSixtySeven).isChecked) {
                val email = view.findViewById<EditText>(R.id.etEmailOne).text.toString()
                val fullName = view.findViewById<EditText>(R.id.etGroupNine).text.toString()
                val password = view.findViewById<EditText>(R.id.etPassword).text.toString()
                val (names, surname) = parseName(fullName)

                if (email.isBlank() || names.isBlank() || surname.isBlank() || password.isBlank()) {
                    CustomSnackbar.showError(view, getString(R.string.missing_input_error))
                } else {
                    signUp(email, names, surname, password, view)
                }
            } else {
                CustomSnackbar.showError(view, getString(R.string.checkbox_not_checked))
            }
        }
        val agreementText = view.findViewById<TextView>(R.id.txtLanguage)
        agreementText.movementMethod = LinkMovementMethod.getInstance()

        val ss = SpannableString(agreementText.text)
        val ulgenStart = agreementText.text.indexOf("Ülgen Kullanım Şartlarını")
        val ulgenEnd = ulgenStart + "Ülgen Kullanım Şartlarını".length
        val privacyStart = agreementText.text.indexOf("Gizlilik Beyanını")
        val privacyEnd = privacyStart + "Gizlilik Beyanını".length

        ss.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {

                AlertDialog.Builder(requireContext())
                    .setMessage(getString(R.string.ulgen_agreement))
                    .setPositiveButton("Tamam", null)
                    .show()
            }
        }, ulgenStart, ulgenEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        ss.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                AlertDialog.Builder(requireContext())
                    .setMessage(getString(R.string.privacy_policy))
                    .setPositiveButton("Tamam", null)
                    .show()
            }
        }, privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        agreementText.text = ss


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
    fun parseName(fullName: String?): Pair<String, String> {
        if (fullName.isNullOrEmpty()) {
            return Pair("", "")
        }

        val nameParts = fullName.split(" ")

        return when {
            nameParts.size >= 2 -> {
                val names = nameParts.subList(0, nameParts.size - 1).joinToString(" ")
                val surname = nameParts.last()
                Pair(names, surname)
            }
            nameParts.size == 1 -> {

                Pair(nameParts[0], "")
            }
            else -> {
                Pair("", "")
            }
        }
    }



}