package tr.edu.ku.ulgen.model.datasource

import android.util.Log

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tr.edu.ku.ulgen.model.apiinterface.ApiInterface
import tr.edu.ku.ulgen.model.kandillilivedatastructure.KandilliEarthquakeLiveData

const val KandilliBaseURL = "https://api.orhanaydogdu.com.tr/"

class KandilliDataSource : KandilliObservable {

    override val observers: ArrayList<KandilliObserver> = ArrayList()

    lateinit var kandilliEarthquakeLiveData: KandilliEarthquakeLiveData

    fun addObserver(observer: KandilliObserver) {
        add(observer)
    }


    fun getMyData() {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(KandilliBaseURL)
            .build()
            .create(ApiInterface::class.java)

        val retrofitData = retrofitBuilder.getData()

        retrofitData.enqueue(object : Callback<KandilliEarthquakeLiveData?> {
            override fun onResponse(
                call: Call<KandilliEarthquakeLiveData?>,
                response: Response<KandilliEarthquakeLiveData?>
            ) {
                val responseBody = response.body()!!


                kandilliEarthquakeLiveData = responseBody

                sendUpdateEvent(kandilliEarthquakeLiveData)


            }

            override fun onFailure(call: Call<KandilliEarthquakeLiveData?>, t: Throwable) {
                Log.d("TAG", "onFailure " + t.message)

            }
        })
    }


}

interface KandilliObserver {
    fun update(kandilliEarthquakeLiveData: KandilliEarthquakeLiveData)
}

interface KandilliObservable {
    val observers: ArrayList<KandilliObserver>

    fun add(observer: KandilliObserver) {
        observers.add(observer)
    }

    fun remove(observer: KandilliObserver) {
        observers.remove(observer)
    }

    fun sendUpdateEvent(kandilliEarthquakeLiveData: KandilliEarthquakeLiveData) {
        observers.forEach { it.update(kandilliEarthquakeLiveData) }
    }
}