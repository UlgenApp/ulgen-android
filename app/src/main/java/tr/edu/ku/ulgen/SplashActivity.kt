package tr.edu.ku.ulgen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tr.edu.ku.ulgen.model.apibodies.UserProfile
import tr.edu.ku.ulgen.model.apiinterface.ApiInterface
import tr.edu.ku.ulgen.model.datasource.UlgenAPIDataSource

class SplashActivity : AppCompatActivity() {

    private lateinit var apiInterface: ApiInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val logoImageView = findViewById<ImageView>(R.id.logoImageView)

        val slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        logoImageView.startAnimation(slideUpAnimation)

        UlgenAPIDataSource.init(this)
        apiInterface = UlgenAPIDataSource.getUlgenAPIData()


        logoImageView.postDelayed({
            checkUserLoginStatus()
        }, 2000L)
    }

    private fun checkUserLoginStatus() {
        apiInterface.getUserProfile().enqueue(object : Callback<UserProfile> {
            override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                if (response.isSuccessful) {
                    navigateToMainActivity(R.id.homeScreenFragment, true)
                } else {
                    navigateToMainActivity(R.id.startScreenFragment, false)
                }
            }

            override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                navigateToMainActivity(R.id.startScreenFragment, false)
            }
        })
    }

    private fun navigateToMainActivity(destinationId: Int, isLoggedIn: Boolean) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("destinationId", destinationId)
            putExtra("isLoggedIn", isLoggedIn)
        }
        startActivity(intent)
        finish()
    }

}



