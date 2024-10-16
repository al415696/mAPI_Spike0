package es.uji.mapi_spike0.ui.home

import android.content.Context
import android.util.Log
import android.view.View
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSource
import es.uji.mapi_spike0.R

class RouteAdder {
    var view: View
    var mapView: MapView
    var context: Context


    constructor(view: View, context: Context){
        this.view = view
        mapView = view.findViewById(R.id.mapView)
        this.context = context
    }
    fun addRoute(allTheGeoJSON: String){
        val style = mapView.mapboxMap.style
        // Create a GeoJSON source from the GeoJSON data
        val source = geoJsonSource("route-source") {
            if (allTheGeoJSON != null) {
                Log.d("Route", "Map extracted")
                data(allTheGeoJSON)
            }
        }
        style?.let {
            // Find the existing GeoJsonSource by ID and update its data
            val geoJsonSource = it.getSource("route-source") as? com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
            geoJsonSource?.data(allTheGeoJSON)
        }
            try {
                mapView.mapboxMap.addSource(source)

            } catch (exception: Exception) {

            }

    }


}