package es.uji.mapi_spike0.ui.home



import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.Bitmap

import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import es.uji.mapi_spike0.R
import com.mapbox.maps.plugin.gestures.GesturesPlugin
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import java.io.InputStream
import kotlin.math.log


class HomeFragment : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var listStyles: List<String>
    private var indexStyle: Int = 0
    private lateinit var style: Style
    private var selectLocationButton: Button? = null
    private var markAdder: MarkerAdder? = null
    private var routeAdder: RouteAdder? = null
    private lateinit var onMapClickListener : TapTest

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        // Find the MapView from the layout
        mapView = view.findViewById(R.id.mapView)

        //initializing interactions
        routeAdder = context?.let { RouteAdder(view, it) }
        markAdder = context?.let { MarkerAdder(view, it) }
        onMapClickListener = TapTest(view, markAdder)
        mapView.mapboxMap.addOnMapClickListener(onMapClickListener)
        //Button test
        view.findViewById<Button>(R.id.selectButton)
            .setOnClickListener {
                // Read the GeoJSON file from the assets folder
                val geoJsonString = loadGeoJsonFromAsset("geojson/testRoute2.geojson")

                if (geoJsonString != null) {
                    routeAdder?.addRoute(geoJsonString)
                }
                Log.d("BUTTONS", "User tapped the button")
            }
        //Different Styles
        listStyles = listOf(Style.MAPBOX_STREETS, Style.SATELLITE, Style.TRAFFIC_DAY,Style.TRAFFIC_NIGHT)
        val cameraOptions = CameraOptions.Builder()
            .center(Point.fromLngLat( -0.06756093559351051,39.99316997818215)) // coordenadas uji
//            .center(Point.fromLngLat( -0.068764, 39.971418,)) // coordenadas alternativas
            .zoom(14.0)
            .bearing(0.0)
            .pitch(0.05)
            .build()
        mapView.mapboxMap.setCamera(cameraOptions)
        //Button test
        view.findViewById<Button>(R.id.styleButton)
            .setOnClickListener {
                indexStyle = (indexStyle+1) % listStyles.size
                mapView.mapboxMap.loadStyle(listStyles[indexStyle]
                )
                Log.d("BUTTONS", "User tapped the button")

            }
        //getting select location button
        selectLocationButton = view.findViewById<Button>(R.id.selectButton)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView.mapboxMap.loadStyle(listStyles[indexStyle]) {

            style ->

            // Create a LineLayer to visualize the route with its style
            val lineLayer = lineLayer("route-layer", "route-source") {
                lineColor("#FF0000")  // Red color for the route
                lineWidth(7.0)        // Line width of 7
                lineOpacity(0.7)      // 70% opacity
                                      // I just like 7
            }

            // Add the LineLayer to the style
            style.addLayer(lineLayer)

            // Add the default UJI mark
            markAdder?.addAnnotationToMap(Point.fromLngLat(-0.06756093559351051,39.99316997818215))
        }
    }
    // Function to load GeoJSON file from the assets folder
    private fun loadGeoJsonFromAsset(fileName: String): String? {
        val inputStream: InputStream =  resources.assets.open(fileName)
        return inputStream.bufferedReader().use { it.readText() }
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


    private fun addAnnotationToMap() {
// Create an instance of the Annotation API and get the PointAnnotationManager.
        context?.let {
            bitmapFromDrawableRes(
                it,
                R.drawable.red_marker
            )?.let {
                val annotationApi = mapView.annotations
                val pointAnnotationManager = annotationApi.createPointAnnotationManager() // se le podría pasar un AnnotationConfig para tener más control
    // Set options for the resulting symbol layer.
                val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
    // Define a geographic coordinate.
                    .withPoint(Point.fromLngLat(-0.06756093559351051,39.99316997818215))
    // Specify the bitmap you assigned to the point annotation
    // The bitmap will be added to map style automatically.
                    .withIconImage(it)
    // Add the resulting pointAnnotation to the map.
                pointAnnotationManager?.create(pointAnnotationOptions)
            }
        }
    }
    public fun addAnnotationToMap(location : Point) {
// Create an instance of the Annotation API and get the PointAnnotationManager.
        context?.let {
            bitmapFromDrawableRes(
                it,
                R.drawable.red_marker
            )?.let {
                val annotationApi = mapView.annotations
                val pointAnnotationManager = annotationApi.createPointAnnotationManager() // se le podría pasar un AnnotationConfig para tener más control
                // Set options for the resulting symbol layer.
                val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                    // Define a geographic coordinate.
                    .withPoint(location)
                    // Specify the bitmap you assigned to the point annotation
                    // The bitmap will be added to map style automatically.
                    .withIconImage(it)
                // Add the resulting pointAnnotation to the map.
                pointAnnotationManager?.create(pointAnnotationOptions)
            }
        }
    }
    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
// copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }
    private var hoveringMarker: ImageView? = null

}