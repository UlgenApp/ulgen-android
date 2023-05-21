package tr.edu.ku.ulgen.model.datasource

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tr.edu.ku.ulgen.model.SharedPreferencesUtil
import tr.edu.ku.ulgen.model.apiinterface.ApiInterface

class UlgenAPIDataSource {
    companion object {
        private const val BASE_URL = "https://api.ulgen.app/"
        private lateinit var sharedPreferencesUtil: SharedPreferencesUtil
        private lateinit var client: OkHttpClient

        fun init(context: Context) {
            sharedPreferencesUtil = SharedPreferencesUtil(context)

            val authInterceptor = okhttp3.Interceptor { chain ->
                val originalRequest = chain.request()

                if (!originalRequest.url.toString().endsWith("api/v1/auth/authenticate")) {
                    val newRequest = originalRequest.newBuilder()
                        .addHeader("Authorization", "Bearer ${sharedPreferencesUtil.getApiToken()}")
                        .build()

                    chain.proceed(newRequest)
                } else {
                    chain.proceed(originalRequest)
                }
            }


            client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .build()
        }

        fun getUlgenAPIData(): ApiInterface {
            check(::sharedPreferencesUtil.isInitialized) { "You need to call init() before using this object" }
            check(::client.isInitialized) { "You need to call init() before using this object" }

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiInterface::class.java)
        }
    }

}

