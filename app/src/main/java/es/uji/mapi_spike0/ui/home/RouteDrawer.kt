package es.uji.mapi_spike0.ui.home

import android.content.Context
import android.view.View
import com.mapbox.geojson.FeatureCollection
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import es.uji.mapi_spike0.R
// Función para dibujar la ruta en el mapa usando el GeoJSON

class RouteDrawer {
    var view: View
    var mapView: MapView
    var context: Context


    constructor(view: View, context: Context){
        this.view = view
        mapView = view.findViewById(R.id.mapView)
        this.context = context
    }
    fun drawRoute(geoJson: FeatureCollection){
        mapView.mapboxMap.getStyle { style ->
            // Obtén el GeoJsonSource existente
            val source = style.getSourceAs<GeoJsonSource>("route-source")

            // Si la fuente existe, actualiza los datos
            if (source != null) {
                source.data(geoJson.toJson())// Actualiza los datos con el nuevo FeatureCollection
            } else {
                // Si la fuente no existe por alguna razón, crea una nueva
                val newSource = GeoJsonSource.Builder("route-source")
                    .featureCollection(geoJson)
                    .build()
                style.addSource(newSource)
            }
        }
    }


}