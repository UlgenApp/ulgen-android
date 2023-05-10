package tr.edu.ku.ulgen.model.datasource

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tr.edu.ku.ulgen.BuildConfig
import tr.edu.ku.ulgen.model.apiinterface.ApiInterface

class RoutingMapDataSource {
    companion object {
        private const val BASE_URL = BuildConfig.ULGEN_BASE_URL
        private const val TOKEN = BuildConfig.TEMP_BEARER_TOKEN

        private val authInterceptor = okhttp3.Interceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $TOKEN")
                .build()

            chain.proceed(newRequest)
        }

        private val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        fun getRoutingMapData(): ApiInterface {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiInterface::class.java)
        }
    }
}
