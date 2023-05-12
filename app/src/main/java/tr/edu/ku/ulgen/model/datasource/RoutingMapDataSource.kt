package tr.edu.ku.ulgen.model.datasource

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tr.edu.ku.ulgen.BuildConfig
import tr.edu.ku.ulgen.model.SharedPreferencesUtil
import tr.edu.ku.ulgen.model.apiinterface.ApiInterface

class RoutingMapDataSource {
    companion object {
        private const val BASE_URL = BuildConfig.ULGEN_BASE_URL
        private lateinit var sharedPreferencesUtil: SharedPreferencesUtil
        private lateinit var client: OkHttpClient

        fun init(context: Context) {

            sharedPreferencesUtil = SharedPreferencesUtil(context)

            val authInterceptor = okhttp3.Interceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${sharedPreferencesUtil.getApiToken()}")
                    .build()

                chain.proceed(newRequest)
            }

            client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .build()
        }

        fun getRoutingMapData(): ApiInterface {
            check(::sharedPreferencesUtil.isInitialized) { "You need to call init(token: String) before using this object" }
            check(::client.isInitialized) { "You need to call init(token: String) before using this object" }

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiInterface::class.java)
        }
    }
}
