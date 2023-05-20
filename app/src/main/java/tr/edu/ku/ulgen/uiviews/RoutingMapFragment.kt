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
import tr.edu.ku.ulgen.BuildConfig
import tr.edu.ku.ulgen.R
import tr.edu.ku.ulgen.uicomponents.CustomInfoWindowAdapter
import java.util.*
import tr.edu.ku.ulgen.model.routingmapdatastructure.RoutingMapResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tr.edu.ku.ulgen.model.datasource.UlgenAPIDataSource
import tr.edu.ku.ulgen.model.routingmapdatastructure.Depot
import tr.edu.ku.ulgen.model.routingmapdatastructure.RoutingMapRequest

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
        // Getting values from previous fragment
        val vehicleCount = arguments?.getString("vehicleCount")?.toIntOrNull() ?: 4
        val latitude = arguments?.getString("latitude")?.toDoubleOrNull() ?: 37.041452
        val longitude = arguments?.getString("longitude")?.toDoubleOrNull() ?: 37.361600
        val seekBarProgress = arguments?.getDouble("seekBarProgress") ?: 0.0
        val distanceCoefficient = 1 - seekBarProgress

        println("AAAAAAAAAA")
        println(distanceCoefficient)
        println(seekBarProgress)

        val depot = Depot(latitude = latitude, longitude = longitude)
        val request = RoutingMapRequest(
            epsilon = 0.002,
            priority_coefficient = seekBarProgress,
            distance_coefficient = distanceCoefficient,
            vehicleCount = vehicleCount,
            depot = depot,
            cities = listOf("Adana", "Hatay")
        )
        UlgenAPIDataSource.init(requireContext())
        val routingMapData = UlgenAPIDataSource.getUlgenAPIData()

        routingMapData.getUserRoute(request).enqueue(object : Callback<RoutingMapResponse> {
            override fun onResponse(call: Call<RoutingMapResponse>, response: Response<RoutingMapResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val result = responseBody?.body?.result
                    val centroidData = result?.centroids
                    val routeData = result?.route

                    // Add your route points
                    val depot = LatLng(depot.latitude, depot.longitude) // Will be taken from prev page
                    val customMarkerBitmap = BitmapFactory.decodeResource(resources, R.drawable.depot)
                    val scaledBitmap = scaleBitmap(customMarkerBitmap, 100, 100)
                    val customMarkerIcon = BitmapDescriptorFactory.fromBitmap(scaledBitmap)

                    mMap.addMarker(MarkerOptions().position(depot).title("Depot").icon(customMarkerIcon))
                    //Will be filled from API call
                    val locations = ArrayList<LatLng>()
                    locations.add(depot)
                    mMap.setInfoWindowAdapter(CustomInfoWindowAdapter(requireContext()))

                    if (centroidData != null) {
                        for (centroid in centroidData) {
                            val latitude = centroid.latitude
                            val longitude = centroid.longitude
                            val priority = centroid.priority

                            val currentLocation = LatLng(latitude, longitude)
                            locations.add(currentLocation)
                            mMap.addMarker(MarkerOptions().position(currentLocation).title("Priority: $priority\nLatitude: $latitude\nLongitude: $longitude"))
                        }
                    }

                    if (routeData != null) {
                        for ((vehicleName, vehicleData) in routeData) {
                            val currentRoutes = vehicleData.route
                            val rnd = Random()
                            val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))

                            for (k in 0 until currentRoutes.size - 1) {
                                val checkIndex = currentRoutes[k+1]
                                if (checkIndex == 0) {
                                    break
                                }

                                val prevLoc = locations[currentRoutes[k]]
                                val nextLoc = locations[checkIndex]

                                drawRouteOnMap(mMap, prevLoc, nextLoc, color)
                            }
                        }
                    }

                    // Move the camera to the depot
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(depot, 5f))
                } else {
                    println("Response failed with status code: ${response.code()}")
                    println("Body: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<RoutingMapResponse>, t: Throwable) {
                println("Failed to get data: ${t.message}")
            }
        })
    }
    private fun scaleBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }
}
