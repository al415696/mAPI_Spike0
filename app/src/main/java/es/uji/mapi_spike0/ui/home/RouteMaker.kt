package es.uji.mapi_spike0.ui.home

import com.mapbox.geojson.GeoJson
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource

interface RouteMaker {
    public fun getRoute(initialPoint: Point, endingPoint: Point) : GeoJsonSource
}