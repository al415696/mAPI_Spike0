package es.uji.mapi_spike0.ui.home

import com.mapbox.geojson.Point
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import java.io.IOException

@RunWith(MockitoJUnitRunner::class)
class RouteAPITest {

    private lateinit var routeAPI: RouteAPI
    @Mock
    private lateinit var mockClient: OkHttpClient
    @Mock
    private lateinit var mockCall: Call
    @Mock
    private lateinit var mockResponse: Response
    @Mock
    private lateinit var mockResponseBody: ResponseBody

    @Before
    fun setUp() {
        mockClient = mock(OkHttpClient::class.java)
        mockCall = mock(Call::class.java)
        mockResponse = mock(Response::class.java)
        mockResponseBody = mock(ResponseBody::class.java)

        routeAPI = RouteAPI()
        routeAPI.client = mockClient
    }

    @Test
    fun getRoute_validPoints() {
        val initialPoint = Point.fromLngLat(-0.127758, 51.507351) // Londres
        val endingPoint = Point.fromLngLat(2.352222, 48.856613) // París

        // Simular la respuesta HTTP exitosa
        whenever(mockClient.newCall(any<Request>())).thenReturn(mockCall)
        whenever(mockCall.execute()).thenReturn(mockResponse)
        whenever(mockResponse.isSuccessful).thenReturn(true)
        whenever(mockResponse.body).thenReturn(mockResponseBody)
        whenever(mockResponseBody.string()).thenReturn(
            """
            {
                "type": "FeatureCollection",
                "features": []
            }
            """.trimIndent()
        )

        val result = routeAPI.getRoute(initialPoint, endingPoint)

        assertNotNull(result)
        assertEquals("FeatureCollection", result.type())
    }

    @Test
    fun getRoute_invalidPoints() {
        val initialPoint = Point.fromLngLat(220.0, 120.0)
        val endingPoint = Point.fromLngLat(370.0, 170.0)

        whenever(mockClient.newCall(any(Request::class.java))).thenReturn(mockCall)
        whenever(mockCall.execute()).thenThrow(IOException("Request failed"))

        val exception = assertThrows(IOException::class.java) {
            routeAPI.getRoute(initialPoint, endingPoint)
        }

        assertEquals("Request failed", exception.message)
    }
}
