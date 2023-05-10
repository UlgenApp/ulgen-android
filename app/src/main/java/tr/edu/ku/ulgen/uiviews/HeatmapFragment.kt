package tr.edu.ku.ulgen.uiviews

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import tr.edu.ku.ulgen.R
import tr.edu.ku.ulgen.model.datasource.HeatMapDataSource
import tr.edu.ku.ulgen.model.heatmapdatastructure.HeatMapRequest
import java.util.ArrayList
import retrofit2.Callback
import tr.edu.ku.ulgen.model.heatmapdatastructure.HeatMapResponse
import retrofit2.Call
import retrofit2.Response

class HeatmapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val heatMapApi = HeatMapDataSource.getHeatMapData()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_heatmap, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.heatmapFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(41.015137, 28.979530), 10f))

        getSampleData()
    }

    private fun getSampleData() {
        val list = ArrayList<WeightedLatLng>()

        //TODO Take cities and epsilon from previous page
        val request = HeatMapRequest(0.002, listOf("Istanbul", "Ankara"))

        heatMapApi.getUserHeatMap(request).enqueue(object: Callback<HeatMapResponse> {
            override fun onResponse(call: Call<HeatMapResponse>, response: Response<HeatMapResponse>) {
                if(response.isSuccessful) {
                    val centroids = response.body()?.body?.result?.centroids
                    centroids?.forEach {
                        println("Priority: ${it.priority}, Latitude: ${it.latitude}, Longitude: ${it.longitude}")
                        list.add(WeightedLatLng(LatLng(it.latitude, it.longitude), it.priority.toDouble()))
                    }
                    addHeatMap(list)
                } else {
                    println("Response failed with status code: ${response.body()}")
                    println("Error body: $response")                }
            }

            override fun onFailure(call: Call<HeatMapResponse>, t: Throwable) {
                println("Failed to get data: ${t.message}")
            }
        })
    }

    private fun addHeatMap(list: List<WeightedLatLng>) {
        val provider = HeatmapTileProvider.Builder()
            .weightedData(list)
            .radius(50)
            .build()

        mMap.addTileOverlay(TileOverlayOptions().tileProvider(provider)) // Change this line
    }

}
