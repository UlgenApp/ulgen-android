package tr.edu.ku.ulgen.model.apiinterface

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Body
import retrofit2.http.POST
import tr.edu.ku.ulgen.model.heatmapdatastructure.HeatMapRequest
import tr.edu.ku.ulgen.model.heatmapdatastructure.HeatMapResponse
import tr.edu.ku.ulgen.model.kandillilivedatastructure.KandilliEarthquakeLiveData

interface ApiInterface {
    @Headers("Accept: application/json")
    @GET("deprem/kandilli/live")
    fun getData(): Call<KandilliEarthquakeLiveData>
    @POST("api/v1/user/heatmap")
    fun getUserHeatMap(@Body request: HeatMapRequest): Call<HeatMapResponse>

}