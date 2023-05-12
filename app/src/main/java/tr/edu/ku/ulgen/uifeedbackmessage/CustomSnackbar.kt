package tr.edu.ku.ulgen.uifeedbackmessage

import android.view.View
import android.widget.TextView
import android.graphics.Color
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.R.id.snackbar_text

class CustomSnackbar {
    companion object {
        fun showError(view: View, message: String) {
            val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
            val snackbarView = snackbar.view
            val textView = snackbarView.findViewById<TextView>(snackbar_text)

            // Customize the Snackbar here
            snackbarView.setBackgroundColor(Color.RED)
            val params = textView.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(0, 0, 0, 0)
            textView.layoutParams = params
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER


            snackbar.show()
        }
        fun showSignUp(view: View, message: String) {
            val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
            val snackbarView = snackbar.view
            val textView = snackbarView.findViewById<TextView>(snackbar_text)

            // Customize the Snackbar here
            val params = textView.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(0, 0, 0, 0)
            textView.layoutParams = params
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER


            snackbar.show()
        }
    }
}
