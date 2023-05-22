package tr.edu.ku.ulgen.uiviews

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tr.edu.ku.ulgen.R
import tr.edu.ku.ulgen.model.SharedPreferencesUtil
import tr.edu.ku.ulgen.model.apibodies.UserSafetyStatus
import tr.edu.ku.ulgen.model.datasource.UlgenAPIDataSource
import tr.edu.ku.ulgen.networkscanner.workers.MACScanWorker
import tr.edu.ku.ulgen.uifeedbackmessage.CustomSnackbar

class IAmSafeScreenFragment : Fragment() {

    private var toggled = false
    private lateinit var safeText: TextView
    private lateinit var imageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_i_am_safe_screen, container, false)

        safeText = view.findViewById(R.id.txtGvendemisiniz)
        imageView = view.findViewById<ImageView>(R.id.imageEllipseEleven)
        imageView.setOnClickListener {
            toggleButton(it)
        }

        updateUserSafetyStatusText()

        return view
    }

    private fun updateUserSafetyStatusText() {
        val isSafe = SharedPreferencesUtil(requireContext()).getUserSafetyStatus()?.isSafe ?: false
        toggled = isSafe
        animateButton(imageView, if(isSafe) 1.2f else 1f)

        if (isSafe) {
            safeText.text = getString(R.string.lbl_i_am_safe)
            safeText.setTextColor(ContextCompat.getColor(requireContext(), R.color.pastel_green))
        } else {
            safeText.text = getString(R.string.msg_g_vende_misiniz)
            safeText.setTextColor(ContextCompat.getColor(requireContext(), R.color.indigo_700))
        }
    }

    private fun toggleButton(view: View) {
        if (toggled) {
            animateButton(view, 1f)
            if(SharedPreferencesUtil(requireContext()).getUserSafetyStatus()?.isSafe == false){
                MACScanWorker.schedule(requireContext())
            }
            saveUserSafetyStatus(false)
            toggled = false
            updateUserSafetyStatusText()
        } else {
            markUserAsSafe(view)
        }
    }

    private fun markUserAsSafe(view: View) {
        val call = UlgenAPIDataSource.getUlgenAPIData().markUserSafe()
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful || response.code() == 400) {
                    MACScanWorker.cancel(requireContext())
                    animateButton(view, 1.2f)
                    toggled = true
                    saveUserSafetyStatus(true)
                    updateUserSafetyStatusText()
                } else {
                    when (response.code()) {
                        409 -> {
                            CustomSnackbar.showError(view, "Etkilenen bölgelerden birinde değilsiniz.")
                        }
                        417 -> {
                            CustomSnackbar.showError(view, "Şu an için herhangi bir afet bildirilmedi.")
                        }
                        else -> {
                            CustomSnackbar.showError(view, "Bilinmeyen bir hata oluştu.")
                            Log.d("bilinmeyen error", response.code().toString())
                        }
                    }
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                CustomSnackbar.showError(view, "Bağlantı hatası")
            }
        })
    }

    private fun saveUserSafetyStatus(isSafe: Boolean) {
        val userProfile = SharedPreferencesUtil(requireContext()).getUserProfile()
        userProfile?.let {
            val userSafetyStatus = UserSafetyStatus(it.email, isSafe)
            SharedPreferencesUtil(requireContext()).saveUserSafetyStatus(userSafetyStatus)
        }
    }

    private fun animateButton(view: View, scale: Float) {
        val animatorSet = AnimatorSet()

        val scaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", scale)
        val scaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", scale)

        animatorSet.playTogether(scaleXAnimator, scaleYAnimator)
        animatorSet.duration = 500
        animatorSet.start()
    }
}
