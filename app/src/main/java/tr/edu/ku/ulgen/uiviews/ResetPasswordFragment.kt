package tr.edu.ku.ulgen.uiviews

import android.os.Bundle
import android.util.Patterns
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
import tr.edu.ku.ulgen.model.apibodies.ForgotPasswordBody
import tr.edu.ku.ulgen.model.datasource.UlgenAPIDataSource
import tr.edu.ku.ulgen.uifeedbackmessage.CustomSnackbar

class ResetPasswordFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reset_password_screen, container, false)

        val resetPasswordButton = view.findViewById<Button>(R.id.btnBalantyGnder)
        val loginText = view.findViewById<TextView>(R.id.txtGiriyap)
        val emailEditText = view.findViewById<EditText>(R.id.etEmailOne)

        loginText.setOnClickListener {
            findNavController().navigate(R.id.action_resetPasswordFragment_to_loginScreenFragment)
        }

        resetPasswordButton.setOnClickListener {
            val email = emailEditText.text.toString()

            if (isValidEmail(email)) {
                forgotPassword(email, view)
            } else {
                CustomSnackbar.showError(view, "Lütfen geçerli bir email adresi girin")
            }
        }

        return view
    }

    private fun isValidEmail(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    private fun forgotPassword(email: String, view: View) {
        val call = UlgenAPIDataSource.getUlgenAPIData().forgotPassword(ForgotPasswordBody(email))

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {

                    CustomSnackbar.showInfo(view, "Şifre sıfırlama e-postası gönderildi")
                } else {

                    CustomSnackbar.showError(view, "Şifre sıfırlama e-postası gönderilemedi")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                CustomSnackbar.showError(view, "Şifre sıfırlama e-postası gönderilemedi")
            }
        })
    }
}
