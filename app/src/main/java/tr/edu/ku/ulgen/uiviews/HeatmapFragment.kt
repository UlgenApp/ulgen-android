package tr.edu.ku.ulgen.uiviews

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tr.edu.ku.ulgen.R
import tr.edu.ku.ulgen.model.SharedPreferencesUtil
import tr.edu.ku.ulgen.model.datasource.AffectedCitiesDataSource
import tr.edu.ku.ulgen.model.datasource.DataSource
import tr.edu.ku.ulgen.model.datasource.UlgenAPIDataSource
import tr.edu.ku.ulgen.model.heatmapdatastructure.HeatMapRequest
import tr.edu.ku.ulgen.model.heatmapdatastructure.HeatMapResponse
import tr.edu.ku.ulgen.uifeedbackmessage.CustomSnackbar

class HeatmapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var view: View
    var items = arrayOf<String>()
    var checkedItems = BooleanArray(items.size)
    private lateinit var loadingFrame: FrameLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_heatmap, container, false)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingFrame = view.findViewById(R.id.loading_frame)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.heatmapFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        loadingFrame.visibility = View.VISIBLE
        mMap = googleMap
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(39.359832, 35.473356), 5f))

        val apiInterface = DataSource.getApiInterface()
        val sharedPreferencesUtil = SharedPreferencesUtil(requireContext())
        val dataSource = AffectedCitiesDataSource(apiInterface, sharedPreferencesUtil)

        dataSource.getAffectedCities(
            onSuccess = { affectedCities ->
                items = affectedCities.toTypedArray()
                checkedItems = BooleanArray(items.size)

                if (checkedItems.isNotEmpty()) {
                    getSampleData()
                }
            },
            onError = { errorMessage ->
                Log.d("affectedcities", "error")
                CustomSnackbar.showError(view, "Şu an için herhangi bir afet bildirilmedi.")
            }
        )
    }

    private fun getSampleData() {
        loadingFrame.visibility = View.VISIBLE
        val list = ArrayList<WeightedLatLng>()

        val request = HeatMapRequest(0.002)

        UlgenAPIDataSource.init(requireContext())
        val heatMapData = UlgenAPIDataSource.getUlgenAPIData()
        heatMapData.getUserHeatMap(request).enqueue(object : Callback<HeatMapResponse> {
            override fun onResponse(
                call: Call<HeatMapResponse>,
                response: Response<HeatMapResponse>
            ) {
                if (response.isSuccessful) {
                    val centroids = response.body()?.body?.result?.centroids
                    centroids?.forEach {
                        list.add(
                            WeightedLatLng(
                                LatLng(it.latitude, it.longitude),
                                it.priority.toDouble()
                            )
                        )
                    }
                    addHeatMap(list)
                } else {
                    println("Response failed with status code: ${response.code()}")
                    println("Body: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<HeatMapResponse>, t: Throwable) {
                println("Failed to get data: ${t.message}")
                hideLoading()
            }
        })
    }

    private fun addHeatMap(list: List<WeightedLatLng>) {
        val provider = HeatmapTileProvider.Builder()
            .weightedData(list)
            .radius(50)
            .build()

        mMap.addTileOverlay(TileOverlayOptions().tileProvider(provider))
        hideLoading()
    }

    private fun hideLoading() {
        loadingFrame.visibility = View.GONE
    }

}
