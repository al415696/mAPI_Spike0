package es.uji.mapi_spike0.ui.home

import com.mapbox.geojson.Point

interface RouteMaker {
    public fun getRoute(initialPoint: Point, endingPoint: Point) : String //Todo el GeoJson en String, no con la cabecera
}