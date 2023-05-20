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

        priorityTextView.text = if ((titleParts?.size ?: 0) > 0) titleParts?.get(0) else "N/A"
        latitudeTextView.text = if ((titleParts?.size ?: 0) > 1) titleParts?.get(1) else "N/A"
        longitudeTextView.text = if ((titleParts?.size ?: 0) > 2) titleParts?.get(2) else "N/A"


        return view
    }

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }
}