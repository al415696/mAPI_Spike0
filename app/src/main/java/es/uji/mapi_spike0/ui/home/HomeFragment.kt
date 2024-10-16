package es.uji.mapi_spike0.ui.home

import android.graphics.Bitmap

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.LineLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import es.uji.mapi_spike0.R
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.IOException


class HomeFragment : Fragment() {

    private lateinit var mapView: MapView
    // Para gestionar los estilos del mapa
    private val listStyles = listOf(Style.MAPBOX_STREETS, Style.SATELLITE, Style.TRAFFIC_DAY,Style.TRAFFIC_NIGHT)
    private var indexStyle: Int = 0
    // Lista para almacenar los marcadores
    private val markers = mutableListOf<PointAnnotation>()
    // Gestor de marcadores en el mapa
    private lateinit var pointAnnotationManager: PointAnnotationManager
    // Ruta API (usaremos la que ya creaste)
    private val routeAPI = RouteAPI()
    // Source de datos de ruta
    private var geoJsonSource = GeoJsonSource.Builder("route-source")
        .featureCollection(FeatureCollection.fromFeatures(emptyList())) // Inicial vacío
        .build()
    //Current set of gasolineras in memory
    private lateinit var gasolineras : PriceGetter.GasolinerasManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        mapView = view.findViewById(R.id.mapView)
        // Obtén el PointAnnotationManager
        pointAnnotationManager = mapView.annotations.createPointAnnotationManager()

        // Configuración inicial del mapa
        val cameraOptions = CameraOptions.Builder()
            // Coordenadas UJI, punto de inicio del mapa
            .center(Point.fromLngLat( -0.06756093559351051,39.99316997818215))
            .zoom(14.0)
            .bearing(0.0)
            .pitch(0.05)
            .build()
        mapView.mapboxMap.setCamera(cameraOptions)


        // Cambiar el diseño cada vez que se pulsa el botón
        view.findViewById<Button>(R.id.styleButton)
            .setOnClickListener {
                indexStyle = (indexStyle+1) % listStyles.size
                mapView.mapboxMap.loadStyle(listStyles[indexStyle]) {style ->
                    style.addSource(geoJsonSource)

                    // Añadir la capa de línea de nuevo
                    val lineLayer = LineLayer("route-layer", "route-source").apply {
                        lineColor(Color.parseColor("#FF0000"))  // Color de la línea (rojo)
                        lineWidth(5.0)  // Ancho de la línea
                    }
                    style.addLayer(lineLayer)
                }
                Log.d("BUTTONS", "User tapped the button")
            }

        // Marcadores en el mapa
        mapView.mapboxMap.loadStyleUri(Style.MAPBOX_STREETS) { style ->
            // Add click listener for map
            mapView.mapboxMap.addOnMapClickListener { point ->
                addMarkerToMap(point)
                true
            }
        }

        // Rutas en el mapa
        val lineLayer = LineLayer("route-layer", "route-source").apply {
            lineColor(Color.parseColor("#FF0000"))  // Color de la línea (rojo)
            lineWidth(5.0)  // Ancho de la línea
        }

        // Añadir el GeoJsonSource y LineLayer al mapa
        mapView.mapboxMap.getStyle { style ->
            style.addSource(geoJsonSource)
            style.addLayer(lineLayer)
        }

        // Activar función en debbuging
        view.findViewById<Button>(R.id.priceButton)
            .setOnClickListener {
            val pricer: PriceGetter = PriceGetter()
                view.findViewById<TextView>(R.id.oil_price).text = "Buscando..."

                runBlocking { // this: CoroutineScope
                launch {
                        gasolineras = pricer.obtenerPreciosCarburantesMadrid()
                            ?.let { it1 -> PriceGetter.GasolinerasManager(it1) }!!
                        Log.d("BUTTONS", "HERE!!")
                        println("gasolineras: " + gasolineras.gasolineras)
                        println("markers: " + markers.size)
                        var texto = gasolineras.getClosest(markers[0].point).precioProducto.toString()
                        println(texto)
                        view.findViewById<TextView>(R.id.oil_price).text = texto + "€"
                    }

            }

            Log.d("BUTTONS", "User tapped the button")
        }

        return view
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

    private fun addMarkerToMap(location: Point) {
        // Convierte el Drawable en Bitmap, no vale la función toBitmap  por que mi telefono es Android 9 :C
        val bitmap = convertDrawableToBitmap(AppCompatResources.getDrawable(requireContext(), R.drawable.red_marker))

        bitmap?.let { iconBitmap ->
            // Si ya hay 2 marcadores, elimina el primero (más antiguo)
            if (markers.size == 2) {
                val removedMarker = markers.removeAt(0) // Elimina el primer marcador
                pointAnnotationManager.delete(removedMarker)  // Elimina el marcador del mapa
            }

            // Crea las opciones para el marcador
            val pointAnnotationOptions = PointAnnotationOptions()
                .withPoint(location)
                // Usar el Bitmap aquí
                .withIconImage(iconBitmap)

            // Crea el marcador
            val marker = pointAnnotationManager.create(pointAnnotationOptions)

            // Agregar el marcador a la lista
            markers.add(marker)

            if (markers.size == 2) {
                val startPoint = markers[0].point
                val endPoint = markers[1].point
                println("Vamos a llamar a la API de rutas")

                // Usar RouteAPI para obtener la ruta entre los dos puntos
                getRouteAndDraw(startPoint, endPoint)
            }

            // Configurar el listener para el clic en el marcador
            pointAnnotationManager.addClickListener { clickedMarker ->
                clickedMarker?.let {
                    // Si el marcador tocado está en la lista, elimínalo
                    pointAnnotationManager.delete(it)
                    markers.remove(it)
                }
                // Devuelve true para indicar que el evento se ha manejado, no necesita buscar más listeners
                true
            }
        }
    }
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

    // Función para obtener la ruta entre dos puntos y dibujarla
    private fun getRouteAndDraw(startPoint: Point, endPoint: Point) {
        try {
            // Llamar a la API de rutas para obtener la ruta
            val route = routeAPI.getRoute(startPoint, endPoint)
            Log.d("GeoJson Response", route.toString())

            // Dibujar la ruta en el mapa si obtenemos la respuesta en GeoJSON
            drawRouteOnMap(route)
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("Error", "Error obteniendo la ruta: ${e.message}")
        }
    }
    // Función para dibujar la ruta en el mapa usando el GeoJSON
    private fun drawRouteOnMap(geoJson: FeatureCollection) {
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