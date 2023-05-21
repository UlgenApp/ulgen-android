package tr.edu.ku.ulgen.uiviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import tr.edu.ku.ulgen.R
import tr.edu.ku.ulgen.model.datasource.IObserver
import tr.edu.ku.ulgen.model.datasource.KandilliDataSource
import tr.edu.ku.ulgen.model.kandillilivedatastructure.KandilliEarthquakeLiveData
import tr.edu.ku.ulgen.recyclerview.KandilliRecyclerviewAdapter


class KandilliRecyclerViewFragment : Fragment(), IObserver {

    private lateinit var newRecyclerView: RecyclerView
    lateinit var swipeContainer: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_kandilli_table, container, false)

        swipeContainer = view.findViewById(R.id.swipeContainer)


        val dataSource = KandilliDataSource()

        dataSource.getMyData()
        dataSource.addObserver(this)

        newRecyclerView = view.findViewById(R.id.recyclerview)
        newRecyclerView.layoutManager = LinearLayoutManager(view.context)
        newRecyclerView.setHasFixedSize(true)

        swipeContainer.setOnRefreshListener {
            dataSource.getMyData()
            swipeContainer.isRefreshing = false
        }

        swipeContainer.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        );

        return view
    }


    override fun update(kandilliEarthquakeLiveData: KandilliEarthquakeLiveData) {
        newRecyclerView.adapter = KandilliRecyclerviewAdapter(kandilliEarthquakeLiveData)


    }

}