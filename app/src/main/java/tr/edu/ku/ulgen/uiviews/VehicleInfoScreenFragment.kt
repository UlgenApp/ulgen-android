package tr.edu.ku.ulgen.uiviews

import DataSource
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import tr.edu.ku.ulgen.R
import tr.edu.ku.ulgen.datasource.AffectedCitiesDataSource
import tr.edu.ku.ulgen.model.SharedPreferencesUtil
import tr.edu.ku.ulgen.uifeedbackmessage.CustomSnackbar

class VehicleInfoScreenFragment : Fragment() {

    private lateinit var seekBar: SeekBar
    var items = arrayOf<String>()
    var checkedItems = BooleanArray(items.size)
    var selectedItems = arrayListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_vehicle_info_screen, container, false)

        val apiInterface = DataSource.getApiInterface()
        val sharedPreferencesUtil = SharedPreferencesUtil(requireContext())
        val dataSource = AffectedCitiesDataSource(apiInterface, sharedPreferencesUtil)

        dataSource.getAffectedCities(
            onSuccess = { affectedCities ->
                items = affectedCities.toTypedArray()


                checkedItems = BooleanArray(items.size)


            },
            onError = { errorMessage ->
                Log.d("affectedcities", "error")
            }
        )

        val multiSelectButton: LinearLayout = view.findViewById(R.id.multiSelectButton)

        multiSelectButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Şehirleri Seç")
                .setMultiChoiceItems(items, checkedItems) { _, which, isChecked ->
                    if (isChecked) {
                        selectedItems.add(items[which])
                    } else if (selectedItems.contains(items[which])) {
                        selectedItems.remove(items[which])
                    }
                }
                .setPositiveButton("Tamam") { _, _ ->

                }
                .setNeutralButton("İptal") { _, _ ->
                    selectedItems.clear()
                }
                .show()
        }

        val vehicleCount = view.findViewById<EditText>(R.id.vehicleCount)
        val latitude = view.findViewById<EditText>(R.id.latitude)
        val longitude = view.findViewById<EditText>(R.id.longitude)

        seekBar = view.findViewById(R.id.linearControl)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val button = view.findViewById<Button>(R.id.btnTamam)

        button.setOnClickListener {
            val vehicleCountStr = vehicleCount.text.toString()
            val latitudeStr = latitude.text.toString()
            val longitudeStr = longitude.text.toString()
            val seekBarProgress = seekBar.progress.toDouble() / seekBar.max

            var errorMessage = ""


            if (vehicleCountStr.isEmpty() || latitudeStr.isEmpty() || longitudeStr.isEmpty()) {
                errorMessage = "Lütfen tüm alanları doldurunuz."
            } else if ((vehicleCountStr.toIntOrNull() ?: 0) <= 1) {
                errorMessage = "Araç sayısı 1'den büyük olmalıdır."
            } else if (latitudeStr.toDoubleOrNull()!! !in -90.0..90.0 || longitudeStr.toDoubleOrNull()!! !in -180.0..180.0) {
                errorMessage = "Geçersiz enlem veya boylam."
            } else if (selectedItems.isEmpty()) {
                errorMessage = "En az bir şehir seçiniz."
            }

            if (errorMessage.isNotEmpty()) {
                CustomSnackbar.showError(view, errorMessage)
            } else {
                val bundle = Bundle().apply {
                    putString("vehicleCount", vehicleCountStr)
                    putString("latitude", latitudeStr)
                    putString("longitude", longitudeStr)
                    putDouble("seekBarProgress", seekBarProgress)
                    putSerializable("cityList", selectedItems)
                }

                findNavController().navigate(
                    R.id.action_vehicleInfoScreenFragment_to_routingMapFragment,
                    bundle
                )
            }
        }




        return view
    }
}
