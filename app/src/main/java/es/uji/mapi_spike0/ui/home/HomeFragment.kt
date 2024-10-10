package es.uji.mapi_spike0.ui.home

import android.content.Context
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
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.FreeCameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import es.uji.mapi_spike0.MainActivity
import es.uji.mapi_spike0.R

class HomeFragment : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var listStyles: List<String>
    private var indexStyle: Int = 0
    private lateinit var style: Style

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
        //Different Styles
        listStyles = listOf(Style.MAPBOX_STREETS, Style.SATELLITE, Style.TRAFFIC_DAY,Style.TRAFFIC_NIGHT)
        val cameraOptions = CameraOptions.Builder()
            .center(Point.fromLngLat( -0.06756093559351051,39.99316997818215))
            .zoom(14.0)
            .bearing(0.0)
            .pitch(30.0)
            .build()
        mapView.mapboxMap.setCamera(cameraOptions)
        //Button test
        view.findViewById<Button>(R.id.button)
            .setOnClickListener {
                indexStyle = (indexStyle+1) % listStyles.size
                mapView.mapboxMap.loadStyle(listStyles[indexStyle]
                )
                Log.d("BUTTONS", "User tapped the button")

            }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load the map with a style
//        mapView.mapboxMap.loadStyle(Style.MAPBOX_STREETS)
        mapView.mapboxMap.loadStyle(listStyles[indexStyle]) { addAnnotationToMap() }
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
}