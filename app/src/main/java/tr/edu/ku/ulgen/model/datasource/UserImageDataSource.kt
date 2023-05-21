package tr.edu.ku.ulgen.model.datasource


import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tr.edu.ku.ulgen.model.SharedPreferencesUtil
import tr.edu.ku.ulgen.model.apiinterface.ApiInterface

class UserImageDataSource(context: Context) {
    private val client = OkHttpClient.Builder().build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.dicebear.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    private val service: ApiInterface = retrofit.create(ApiInterface::class.java)
    private val sharedPreferencesUtil = SharedPreferencesUtil(context)

    fun getUserImage(): Call<ResponseBody> {
        val email = sharedPreferencesUtil.getUserProfile()?.email ?: "ulgen"
        return service.getUserImage(email)
    }
}
