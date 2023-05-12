package tr.edu.ku.ulgen.model

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import tr.edu.ku.ulgen.model.apibodies.UserProfile

class SharedPreferencesUtil(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "my_prefs"
        private const val API_TOKEN = "api_token"
        private const val USER_PROFILE = "user_profile"
    }

    // Function to save API token
    fun saveApiToken(token: String) {
        prefs.edit().putString(API_TOKEN, token).apply()
    }

    // Function to fetch API token
    fun getApiToken(): String? {
        return prefs.getString(API_TOKEN, null)
    }

    // Function to clear API token
    fun clearApiToken() {
        prefs.edit().remove(API_TOKEN).apply()
    }

    // Function to save user profile
    fun saveUserProfile(userProfile: UserProfile) {
        val userProfileJson = Gson().toJson(userProfile)
        prefs.edit().putString(USER_PROFILE, userProfileJson).apply()
    }

    // Function to fetch user profile
    fun getUserProfile(): UserProfile? {
        val userProfileJson = prefs.getString(USER_PROFILE, null)
        return Gson().fromJson(userProfileJson, UserProfile::class.java)
    }

    // Function to clear user profile
    fun clearUserProfile() {
        prefs.edit().remove(USER_PROFILE).apply()
    }
}
