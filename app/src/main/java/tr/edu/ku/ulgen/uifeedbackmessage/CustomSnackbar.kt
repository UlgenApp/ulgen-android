package tr.edu.ku.ulgen.uifeedbackmessage

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.R.id.snackbar_text
import com.google.android.material.snackbar.Snackbar

class CustomSnackbar {
    companion object {
        fun showError(view: View, message: String) {
            val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
            val snackbarView = snackbar.view
            val textView = snackbarView.findViewById<TextView>(snackbar_text)

            snackbarView.setBackgroundColor(Color.rgb(205, 92, 92))
            val params = textView.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(0, 0, 0, 0)
            textView.layoutParams = params
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER

            snackbar.show()
        }

        fun showInfo(view: View, message: String) {
            val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
            val snackbarView = snackbar.view
            val textView = snackbarView.findViewById<TextView>(snackbar_text)

            snackbarView.setBackgroundColor(Color.rgb(119, 221, 119))
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

            val params = textView.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(0, 0, 0, 0)
            textView.layoutParams = params
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER

            snackbar.show()
        }
    }
}
