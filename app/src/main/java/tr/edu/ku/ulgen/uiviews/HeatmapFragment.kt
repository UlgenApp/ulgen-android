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
import java.util.ArrayList

class HeatmapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

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

        // Add a heatmap to the map
        addHeatMap()
    }

    private fun addHeatMap() {
        val list = getSampleData()

        val provider = HeatmapTileProvider.Builder()
            .weightedData(list)
            .radius(50)
            .build()

        mMap.addTileOverlay(TileOverlayOptions().tileProvider(provider)) // Change this line
    }


    private fun getSampleData(): List<WeightedLatLng> {
        val list = ArrayList<WeightedLatLng>()

        // Add some sample data to the list
        list.add(WeightedLatLng(LatLng(38.423733, 27.142826), 30.0))
        list.add(WeightedLatLng(LatLng(37.0, 35.321335), 10.0))
        list.add(WeightedLatLng(LatLng(37.575275, 36.922821), 50.0))

        return list
    }
}
