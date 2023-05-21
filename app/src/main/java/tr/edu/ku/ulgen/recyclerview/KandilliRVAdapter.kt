package tr.edu.ku.ulgen.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tr.edu.ku.ulgen.R
import tr.edu.ku.ulgen.model.kandillilivedatastructure.KandilliEarthquakeLiveData


class KandilliRecyclerviewAdapter(private val earthquakeList: KandilliEarthquakeLiveData) :
    RecyclerView.Adapter<KandilliRecyclerviewAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.kandilli_recyclerview_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return earthquakeList.result.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = earthquakeList.result[position]
        holder.region.text = currentItem.title
        holder.magnitude.text = currentItem.mag.toString()
        holder.date.text = currentItem.date_time

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.date)
        val region: TextView = itemView.findViewById(R.id.region)
        val magnitude: TextView = itemView.findViewById(R.id.magnitude)
    }
}