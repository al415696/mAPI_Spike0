package es.uji.mapi_spike0.ui.home

import android.view.View
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.gestures.OnMapClickListener

class TapTest public constructor(var view: View, var markerAdder: MarkerAdder?) : OnMapClickListener {

    override fun onMapClick(point: Point): Boolean {
        markerAdder?.addAnnotationToMap(point)
        return false
    }

}