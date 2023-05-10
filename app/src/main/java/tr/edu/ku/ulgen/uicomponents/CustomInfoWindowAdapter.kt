package tr.edu.ku.ulgen.uicomponents

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import tr.edu.ku.ulgen.R

class CustomInfoWindowAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {

    override fun getInfoContents(marker: Marker): View {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null)

        val priorityTextView = view.findViewById<TextView>(R.id.priorityTextView)
        val latitudeTextView = view.findViewById<TextView>(R.id.latitudeTextView)
        val longitudeTextView = view.findViewById<TextView>(R.id.longitudeTextView)

        val titleParts = marker.title?.split("\n")

        priorityTextView.text = titleParts?.get(0) ?: "N/A"
        latitudeTextView.text = titleParts?.get(1) ?: "N/A"
        longitudeTextView.text = titleParts?.get(2) ?: "N/A"

        return view
    }

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }
}