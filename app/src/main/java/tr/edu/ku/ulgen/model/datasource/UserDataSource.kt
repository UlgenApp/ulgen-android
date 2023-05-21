package tr.edu.ku.ulgen.model.datasource

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tr.edu.ku.ulgen.BuildConfig
import tr.edu.ku.ulgen.model.apibodies.UserProfile
import tr.edu.ku.ulgen.model.apiinterface.ApiInterface

class UserDataSource {
    private val BASE_URL: String = "https://api.ulgen.app/"

    private val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }

    private val client: OkHttpClient = OkHttpClient.Builder().apply {
        this.addInterceptor(interceptor)
    }.build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiInterface: ApiInterface = retrofit.create(ApiInterface::class.java)

    fun getUserProfile(bearerToken: String): UserProfile? {
        val call = apiInterface.getUserProfile()
        val response = call.execute()
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }
}

