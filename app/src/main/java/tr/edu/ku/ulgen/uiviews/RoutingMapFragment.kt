package tr.edu.ku.ulgen.uiviews

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.android.PolyUtil
import com.google.maps.model.TravelMode
import org.json.JSONObject
import tr.edu.ku.ulgen.BuildConfig
import tr.edu.ku.ulgen.R
import tr.edu.ku.ulgen.uicomponents.CustomInfoWindowAdapter
import java.util.*

class RoutingMapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_routing_map, container, false)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.routingMapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return view
    }

    private fun drawRouteOnMap(map: GoogleMap, start: LatLng, end: LatLng, color: Int) {
        val apiKey = BuildConfig.MAPS_API_KEY

        val startCoord = com.google.maps.model.LatLng(start.latitude, start.longitude)
        val endCoord = com.google.maps.model.LatLng(end.latitude, end.longitude)
        val geoApiContext = GeoApiContext.Builder()
            .apiKey(apiKey)
            .build()
        val directions = DirectionsApi.newRequest(geoApiContext)
            .mode(TravelMode.DRIVING)
            .origin(startCoord)
            .destination(endCoord)
            .await()

        val polylineOptions = PolylineOptions().addAll(PolyUtil.decode(directions.routes[0].overviewPolyline.encodedPath)).color(color)
        map.addPolyline(polylineOptions)
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val result = getData().getJSONObject("result")
        val centroidData = result.getJSONArray("centroids")
        val routeData = result.getJSONObject("route")
        // Add your route points
        val depot = LatLng(41.015137, 28.979530) // Will be taken from prev page
        val customMarkerBitmap = BitmapFactory.decodeResource(resources, R.drawable.depot)
        val scaledBitmap = scaleBitmap(customMarkerBitmap, 100, 100)
        val customMarkerIcon = BitmapDescriptorFactory.fromBitmap(scaledBitmap)

        mMap.addMarker(MarkerOptions().position(depot).title("Depot").icon(customMarkerIcon))
        //Will be filled from API call
        val locations = ArrayList<LatLng>()
        locations.add(depot)
        mMap.setInfoWindowAdapter(CustomInfoWindowAdapter(requireContext()))


        for (i in 0 until centroidData.length()) {
            val latitude = centroidData.getJSONObject(i).getDouble("latitude")
            val longitude = centroidData.getJSONObject(i).getDouble("longitude")
            val priority = centroidData.getJSONObject(i).getDouble("priority")

            val currentLocation = LatLng(latitude, longitude)
            locations.add(currentLocation)
            mMap.addMarker(MarkerOptions().position(currentLocation).title("Priority: $priority\nLatitude: $latitude\nLongitude: $longitude"))


        }

        for (j in 1..routeData.length()) {
            val currentVehicle = routeData.getJSONObject("vehicle_$j")
            val currentRoutes = currentVehicle.getJSONArray("route")
            val rnd = Random()
            val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))

            for (k in 0 until currentRoutes.length()){
                val checkIndex = currentRoutes.getInt(k+1)
                if (checkIndex == 0){
                    break
                }

                val prevLoc = locations[currentRoutes.getInt(k)]
                val nextLoc = locations[checkIndex]


                drawRouteOnMap(mMap, prevLoc, nextLoc, color)

            }
        }
        // Move the camera to the depot
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(depot, 5f))
    }
    private fun scaleBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }
    fun getData(): JSONObject {
        return JSONObject("{\n" +
                "    \"result\": {\n" +
                "        \"centroids\": [\n" +
                "            {\n" +
                "                \"priority\": 1,\n" +
                "                \"latitude\": 38.423733,\n" +
                "                \"longitude\": 27.142826\n" +
                "            },\n" +
                "            {\n" +
                "                \"priority\": 1,\n" +
                "                \"latitude\": 37.0,\n" +
                "                \"longitude\": 35.321335\n" +
                "            },\n" +
                "            {\n" +
                "                \"priority\": 1,\n" +
                "                \"latitude\": 37.575275,\n" +
                "                \"longitude\": 36.922821\n" +
                "            },\n" +
                "            {\n" +
                "                \"priority\": 1,\n" +
                "                \"latitude\": 37.066666,\n" +
                "                \"longitude\": 37.383331\n" +
                "            },\n" +
                "            {\n" +
                "                \"priority\": 1,\n" +
                "                \"latitude\": 36.891339,\n" +
                "                \"longitude\": 30.712438\n" +
                "            }\n" +
                "        ],\n" +
                "        \"route\": {\n" +
                "            \"vehicle_1\": {\n" +
                "                \"route\": [\n" +
                "                    0,\n" +
                "                    1,\n" +
                "                    0\n" +
                "                ],\n" +
                "                \"distance_travelled\": 672798\n" +
                "            },\n" +
                "            \"vehicle_2\": {\n" +
                "                \"route\": [\n" +
                "                    0,\n" +
                "                    5,\n" +
                "                    0\n" +
                "                ],\n" +
                "                \"distance_travelled\": 977026\n" +
                "            },\n" +
                "            \"vehicle_3\": {\n" +
                "                \"route\": [\n" +
                "                    0,\n" +
                "                    2,\n" +
                "                    0\n" +
                "                ],\n" +
                "                \"distance_travelled\": 1296842\n" +
                "            },\n" +
                "            \"vehicle_4\": {\n" +
                "                \"route\": [\n" +
                "                    0,\n" +
                "                    3,\n" +
                "                    4,\n" +
                "                    0\n" +
                "                ],\n" +
                "                \"distance_travelled\": 1576613\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}\n")
    }
}
