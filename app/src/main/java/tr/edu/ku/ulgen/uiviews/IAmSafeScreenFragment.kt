package tr.edu.ku.ulgen.uiviews

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_i_am_safe_screen, container, false)


        val safeText = view.findViewById<TextView>(R.id.txtGvendemisiniz)

        val imageView = view.findViewById<ImageView>(R.id.imageEllipseEleven)
        imageView.setOnClickListener {
            toggleButton(it)
        }


        return view
    }

    private fun toggleButton(view: View) {
        if (toggled) {
            animateButton(view, 1f)
            if(SharedPreferencesUtil(requireContext()).getUserSafetyStatus()?.isSafe == false){
                MACScanWorker.schedule(requireContext())
            }
            saveUserSafetyStatus(false)
            toggled = false
        } else {
            markUserAsSafe(view)
        }
    }

    private fun markUserAsSafe(view: View) {
        val call = UlgenAPIDataSource.getUlgenAPIData().markUserSafe()
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    MACScanWorker.cancel(requireContext())
                    animateButton(view, 1.2f)
                    toggled = true
                    saveUserSafetyStatus(true)
                } else {
                    when (response.code()) {
                        409 -> {
                            CustomSnackbar.showError(view, "Etkilenen bölgelerden birinde değilsiniz.")
                            print("youre not in danger")
                        }
                        417 -> {
                            CustomSnackbar.showError(view, "Şu an için herhangi bir afet bildirilmedi.")
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