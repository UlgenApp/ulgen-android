package tr.edu.ku.ulgen.Model.Datasource

import android.util.Log

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tr.edu.ku.ulgen.Model.ApiInterface.ApiInterface
import tr.edu.ku.ulgen.Model.KandilliLiveDataStructure.KandilliEarthquakeLiveData

const val KandilliBaseURL = "https://api.orhanaydogdu.com.tr/"

class KandilliDataSource : IObservable {

    override val observers: ArrayList<IObserver> = ArrayList()

    lateinit var kandilliEarthquakeLiveData: KandilliEarthquakeLiveData

    fun addObserver(observer: IObserver) {
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


                Log.d("TAG", kandilliEarthquakeLiveData.result[1].title)
            }

            override fun onFailure(call: Call<KandilliEarthquakeLiveData?>, t: Throwable) {
                Log.d("TAG",  "onFailure " + t.message)

            }
        })
    }


}

interface IObserver {
    fun update(kandilliEarthquakeLiveData: KandilliEarthquakeLiveData)
}

interface IObservable {
    val observers: ArrayList<IObserver>

    fun add(observer: IObserver) {
        observers.add(observer)
    }

    fun remove(observer: IObserver) {
        observers.remove(observer)
    }

    fun sendUpdateEvent(kandilliEarthquakeLiveData: KandilliEarthquakeLiveData) {
        observers.forEach { it.update(kandilliEarthquakeLiveData) }
    }
}