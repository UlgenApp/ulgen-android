package tr.edu.ku.ulgen.model.apiinterface

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import tr.edu.ku.ulgen.model.kandillilivedatastructure.KandilliEarthquakeLiveData

interface ApiInterface {
    @Headers("Accept: application/json")
    @GET("deprem/kandilli/live")
    fun getData(): Call<KandilliEarthquakeLiveData>
}