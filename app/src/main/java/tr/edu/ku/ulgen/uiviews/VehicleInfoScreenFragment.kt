package tr.edu.ku.ulgen.uiviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import tr.edu.ku.ulgen.R

class VehicleInfoScreenFragment : Fragment() {

    private lateinit var seekBar: SeekBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_vehicle_info_screen, container, false)

        seekBar = view.findViewById(R.id.linearControl)
        seekBar.max = 4
        seekBar.progress = 2
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


        return view
    }




}