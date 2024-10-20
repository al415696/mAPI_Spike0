package es.uji.mapi_spike0.ui.home

import android.content.Context
import android.view.View
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.getSource
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import es.uji.mapi_spike0.R
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RouteAdderTest {

    private lateinit var routeAdder: RouteAdder
    private lateinit var mockView: View
    private lateinit var mockMapView: MapView
    private lateinit var mockContext: Context
    private lateinit var mockStyle: Style
    private lateinit var mockGeoJsonSource: GeoJsonSource
    private lateinit var mockMapboxMap: MapboxMap

    @Before
    fun setUp() {
        // Mockear las dependencias
        mockView = mock(View::class.java)
        mockMapView = mock(MapView::class.java)
        mockContext = mock(Context::class.java)
        mockStyle = mock(Style::class.java)
        mockGeoJsonSource = mock(GeoJsonSource::class.java)
        mockMapboxMap = mock(MapboxMap::class.java)

        // Simular el comportamiento de findViewById
        `when`(mockView.findViewById<MapView>(R.id.mapView)).thenReturn(mockMapView)

        // Simular el estilo del mapa
        `when`(mockMapView.mapboxMap).thenReturn(mockMapboxMap)
        `when`(mockMapboxMap.style).thenReturn(mockStyle)

        routeAdder = RouteAdder(mockView, mockContext)
    }

    @Test
    fun testAddValidRoute() {
        // Dato GeoJSON válido
        val validGeoJson = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[102.0, 0.5],[103.0, 0.5]]}}"

        routeAdder.addRoute(validGeoJson)

        verify(mockStyle).addSource(any(GeoJsonSource::class.java))

        val captor = ArgumentCaptor.forClass(GeoJsonSource::class.java)
        verify(mockStyle).addSource(captor.capture())

        assert(captor.value.data == validGeoJson) // Verifica que los datos sean correctos
    }

    /* Por la naturaleza de la clase RouteAdder, la ruta pasada nunca podrá ser nula, para ser así pondríamos "addRoute(allTheGeoJSON: String?)"
    @Test
    fun testAddNullRoute() {
        routeAdder.addRoute(null)

        verify(mockStyle, never()).addSource(any())
    }
    */

    @Test
    fun testAddRouteWithExistingSource() {
        val validGeoJson = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[102.0, 0.5],[103.0, 0.5]]}}"

        `when`(mockStyle.getSource("route-source")).thenReturn(mockGeoJsonSource)

        routeAdder.addRoute(validGeoJson)

        verify(mockGeoJsonSource).data(validGeoJson) // Verifica que se actualice correctamente
    }

    @Test
    fun testAddRouteWithException() {
        val validGeoJson = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[102.0, 0.5],[103.0, 0.5]]}}"

        doThrow(RuntimeException("Error al añadir la fuente")).`when`(mockStyle).addSource(any())

        try {
            routeAdder.addRoute(validGeoJson)
        } catch (e: Exception) {
            assert(false)
        }
    }
}
