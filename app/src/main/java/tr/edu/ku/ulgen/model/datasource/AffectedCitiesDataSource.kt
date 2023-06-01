package tr.edu.ku.ulgen.model.datasource

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tr.edu.ku.ulgen.model.SharedPreferencesUtil
import tr.edu.ku.ulgen.model.apiinterface.ApiInterface

class AffectedCitiesDataSource(
    private val apiInterface: ApiInterface,
    private val sharedPreferencesUtil: SharedPreferencesUtil
) {
    fun getAffectedCities(
        onSuccess: (List<String>) -> Unit,
        onError: (String) -> Unit
    ) {
        val token = sharedPreferencesUtil.getApiToken()

        if (token != null) {
            val call = apiInterface.getAffectedCities("Bearer $token")
            call.enqueue(object : Callback<Map<String, List<String>>> {
                override fun onResponse(
                    call: Call<Map<String, List<String>>>,
                    response: Response<Map<String, List<String>>>
                ) {
                    if (response.isSuccessful) {
                        val affectedCities = response.body()?.get("affectedCities")
                        if (affectedCities != null) {
                            onSuccess(affectedCities)
                        } else {
                            onError("No data found")
                        }
                    } else {
                        onError("Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Map<String, List<String>>>, t: Throwable) {
                    onError(t.message ?: "Unknown error")
                }
            })
        } else {
            onError("Token not found")
        }
    }
}
