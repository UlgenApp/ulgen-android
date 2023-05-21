package tr.edu.ku.ulgen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val logoImageView = findViewById<ImageView>(R.id.logoImageView)

        val slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        logoImageView.startAnimation(slideUpAnimation)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, SPLASH_DISPLAY_LENGTH)
    }

    companion object {
        const val SPLASH_DISPLAY_LENGTH = 3000L
    }
}


