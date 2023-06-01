package tr.edu.ku.ulgen.model.apiinterface

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import tr.edu.ku.ulgen.model.apibodies.*
import tr.edu.ku.ulgen.model.heatmapdatastructure.HeatMapRequest
import tr.edu.ku.ulgen.model.heatmapdatastructure.HeatMapResponse
import tr.edu.ku.ulgen.model.kandillilivedatastructure.KandilliEarthquakeLiveData
import tr.edu.ku.ulgen.model.macscannerdatastructure.MACScannerRequest
import tr.edu.ku.ulgen.model.macscannerdatastructure.MACScannerResponse
import tr.edu.ku.ulgen.model.routingmapdatastructure.RoutingMapRequest
import tr.edu.ku.ulgen.model.routingmapdatastructure.RoutingMapResponse

interface ApiInterface {
    @Headers("Accept: application/json")
    @GET("deprem/kandilli/live")
    fun getData(): Call<KandilliEarthquakeLiveData>

    @POST("api/v1/user/heatmap")
    fun getUserHeatMap(@Body request: HeatMapRequest): Call<HeatMapResponse>

    @POST("api/v1/user/route")
    fun getUserRoute(@Body body: RoutingMapRequest): Call<RoutingMapResponse>

    @Headers("Content-Type:application/json")
    @POST("api/v1/auth/authenticate")
    fun signin(@Body info: SignInBody): Call<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST("api/v1/auth/register")
    fun registerUser(
        @Body info: UserBody
    ): Call<ResponseBody>

    @GET("api/v1/user/profile")
    fun getUserProfile(): Call<UserProfile>

    @POST("api/v1/producer/produce")
    fun sendMACAddresses(@Body requestBody: MACScannerRequest): Call<MACScannerResponse>

    @GET("api/v1/user/affected-cities")
    fun getAffectedCities(@Header("Authorization") token: String): Call<Map<String, List<String>>>

    @GET("6.x/thumbs/png")
    fun getUserImage(@Query("seed") email: String): Call<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST("api/v1/user/update/additional-info")
    fun updateUserAdditionalInfo(@Body body: AdditionalInfoBody): Call<ResponseBody>

    @POST("api/v1/user/safe")
    fun markUserSafe(): Call<Void>

    @Headers("Content-Type:application/json")
    @POST("forgot-password")
    fun forgotPassword(@Body body: ForgotPasswordBody): Call<ResponseBody>

}