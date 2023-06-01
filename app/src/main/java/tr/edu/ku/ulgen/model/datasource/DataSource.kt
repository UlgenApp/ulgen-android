package tr.edu.ku.ulgen.model.datasource

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tr.edu.ku.ulgen.model.apiinterface.ApiInterface

class DataSource {

    companion object {
        private var INSTANCE: ApiInterface? = null
        private const val BASE_URL = "https://api.ulgen.app/"

        fun getApiInterface(): ApiInterface {
            if (INSTANCE == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                INSTANCE = retrofit.create(ApiInterface::class.java)
            }

            return INSTANCE!!
        }
    }
}
