package es.uji.mapi_spike0.ui.home.ors

import com.mapbox.geojson.Point
import es.uji.mapi_spike0.ui.home.RouteMaker
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class RouteAPI: RouteMaker {

    private val client = OkHttpClient()

    override fun getRoute(initialPoint: Point, endingPoint: Point): String {
        val url = "https://api.openrouteservice.org/v2/directions/driving-car/geojson"

        // Coordenadas de origen y destino
        val coordinates = """
            {
              "coordinates": [
                [${initialPoint.longitude()}, ${initialPoint.latitude()}],
                [${endingPoint.longitude()}, ${endingPoint.latitude()}]
              ],
              "preference": "fastest"
            }
        """.trimIndent()

        val requestBody = coordinates.toRequestBody("application/json; charset=utf-8".toMediaType())

        // Crear solicitud POST
        val request = Request.Builder()
            .url(url)
            .header("Authorization", "5b3ce3597851110001cf6248d89338ee73144e65910e6058196bea9c")
            .post(requestBody)
            .build()

        // Ejecutar la solicitud
        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                response.body?.string() ?: ""
            } else {
                "Error: ${response.message}"
            }
        } catch (e: IOException) {
            e.printStackTrace()
            "Request failed: ${e.message}"
        }
    }
}