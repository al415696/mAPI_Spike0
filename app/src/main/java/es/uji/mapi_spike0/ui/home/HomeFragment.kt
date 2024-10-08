package es.uji.mapi_spike0.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import es.uji.mapi_spike0.R

class HomeFragment : Fragment() {

    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Find the MapView from the layout
        mapView = view.findViewById(R.id.mapView)

        //Button test
        view.findViewById<Button>(R.id.button)
            .setOnClickListener {
                Log.d("BUTTONS", "User tapped the button")
            }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load the map with a style
        mapView.mapboxMap.loadStyle(Style.MAPBOX_STREETS)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }
}