package tr.edu.ku.ulgen.uiviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import tr.edu.ku.ulgen.R

class VehicleInfoScreenFragment : Fragment() {

    private lateinit var seekBar: SeekBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_vehicle_info_screen, container, false)

        val vehicleCount = view.findViewById<EditText>(R.id.vehicleCount)
        val latitude = view.findViewById<EditText>(R.id.latitude)
        val longitude = view.findViewById<EditText>(R.id.longitude)

        seekBar = view.findViewById(R.id.linearControl)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Handle the seek bar progress change event
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Handle the seek bar touch event
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Handle the seek bar release event
            }
        })

        val button = view.findViewById<Button>(R.id.btnTamam)

        button.setOnClickListener {
            val vehicleCountStr = vehicleCount.text.toString()
            val latitudeStr = latitude.text.toString()
            val longitudeStr = longitude.text.toString()
            val seekBarProgress = seekBar.progress.toDouble() / seekBar.max

            val bundle = Bundle().apply {
                putString("vehicleCount", vehicleCountStr)
                putString("latitude", latitudeStr)
                putString("longitude", longitudeStr)
                putDouble("seekBarProgress", seekBarProgress)
            }

            findNavController().navigate(R.id.action_vehicleInfoScreenFragment_to_routingMapFragment, bundle)
        }


        return view
    }




}