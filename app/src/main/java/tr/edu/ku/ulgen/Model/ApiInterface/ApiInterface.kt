package tr.edu.ku.ulgen.Model.ApiInterface

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import tr.edu.ku.ulgen.Model.KandilliLiveDataStructure.KandilliEarthquakeLiveData

interface ApiInterface {
    @Headers("Accept: application/json")
    @GET("deprem/kandilli/live")
    fun getData(): Call<KandilliEarthquakeLiveData>
}