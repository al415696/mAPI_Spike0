package es.uji.mapi_spike0.ui.home

import android.content.Context
import android.view.View
import com.google.gson.internal.bind.TypeAdapters.URI
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.FillLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.getLayer
import com.mapbox.maps.extension.style.layers.getLayerAs
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.getSource
import com.mapbox.maps.extension.style.sources.updateGeoJSONSourceFeatures
import com.mapbox.maps.plugin.annotation.AnnotationManager
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
    fun addRoute(string: String, source: GeoJsonSource){
        val style = mapView.mapboxMap.style

        style?.let {
            // Find the existing GeoJsonSource by ID and update its data
            val geoJsonSource = it.getSource("route-source") as? com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
            geoJsonSource?.data(string)
        }
            try {
                mapView.mapboxMap.addSource(source)

            } catch (exception: Exception) {

            }

    }


}