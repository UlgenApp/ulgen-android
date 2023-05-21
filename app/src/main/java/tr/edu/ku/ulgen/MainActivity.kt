package tr.edu.ku.ulgen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tr.edu.ku.ulgen.model.apibodies.UserProfile
import tr.edu.ku.ulgen.model.apiinterface.ApiInterface
import tr.edu.ku.ulgen.model.datasource.UlgenAPIDataSource
import tr.edu.ku.ulgen.networkscanner.workers.MACScanWorker


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var apiInterface: ApiInterface
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        UlgenAPIDataSource.init(this)
        apiInterface = UlgenAPIDataSource.getUlgenAPIData()

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        setupWithNavController(bottomNavigationView, navController)
        // always show selected Bottom Navigation item as selected (return true)
        bottomNavigationView.setOnItemSelectedListener { item ->
            NavigationUI.onNavDestinationSelected(item, navController)

            return@setOnItemSelectedListener true
        }

        val destinationIds = listOf(
            R.id.startScreenFragment,
            R.id.loginScreenFragment,
            R.id.resetPasswordFragment,
            R.id.signUpScreenFragment
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNavigationView.visibility = if (destinationIds.contains(destination.id)) {
                View.GONE
            } else {
                View.VISIBLE
            }


        }
        checkUserLoginStatus()
    }
    private fun checkUserLoginStatus() {
        apiInterface.getUserProfile().enqueue(object : Callback<UserProfile> {
            override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                if (response.isSuccessful) {
                    // If the response is successful, navigate to home screen.
                    navController.navigate(R.id.homeScreenFragment)
                } else {
                    // If the response is not successful, navigate to start screen.
                    navController.navigate(R.id.startScreenFragment)
                }
            }

            override fun onFailure(call: Call<UserProfile>, t: Throwable) {
                // If there is any error, navigate to start screen.
                navController.navigate(R.id.startScreenFragment)
            }
        })
    }

}