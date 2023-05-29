package tr.edu.ku.ulgen

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tr.edu.ku.ulgen.model.SharedPreferencesUtil
import tr.edu.ku.ulgen.model.apibodies.UserProfile
import tr.edu.ku.ulgen.model.apiinterface.ApiInterface
import tr.edu.ku.ulgen.model.datasource.UlgenAPIDataSource
import tr.edu.ku.ulgen.uiviews.*


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

        bottomNavigationView.setOnItemSelectedListener { item ->
            NavigationUI.onNavDestinationSelected(item, navController)

            return@setOnItemSelectedListener true
        }

        val destinationId = intent.getIntExtra("destinationId", R.id.homeScreenFragment)
        navController.navigate(destinationId)

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
    override fun onStop() {
        super.onStop()
        SharedPreferencesUtil(this).setMACAddressesSent(false)
    }

    override fun onBackPressed() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        val currentFragment = navHostFragment.childFragmentManager.fragments[0]

        if (currentFragment is KandilliRecyclerViewFragment ||
            currentFragment is VehicleInfoScreenFragment ||
            currentFragment is HomeScreenFragment ||
            currentFragment is HeatmapFragment ||
            currentFragment is IAmSafeScreenFragment
        ) {
            Log.d("Back Button", "Back button is blocked. ")
        } else {
            super.onBackPressed()
        }
    }

    private fun checkUserLoginStatus() {
        apiInterface.getUserProfile().enqueue(object : Callback<UserProfile> {
            override fun onResponse(call: Call<UserProfile>, response: Response<UserProfile>) {
                if (response.isSuccessful) {

                    navController.navigate(R.id.homeScreenFragment)
                } else {

                    navController.navigate(R.id.startScreenFragment)
                }
            }

            override fun onFailure(call: Call<UserProfile>, t: Throwable) {

                navController.navigate(R.id.startScreenFragment)
            }
        })
    }

}