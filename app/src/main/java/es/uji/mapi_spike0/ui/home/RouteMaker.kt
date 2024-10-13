package es.uji.mapi_spike0.ui.home

import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point

interface RouteMaker {
    public fun getRoute(initialPoint: Point, endingPoint: Point) : FeatureCollection
}