package es.uji.mapi_spike0.ui.home

import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class RouteAPI: RouteMaker {

    private val client = OkHttpClient()

    override fun getRoute(initialPoint: Point, endingPoint: Point): FeatureCollection {
// Lanzamos un Coroutine en el hilo de fondo
        return runBlocking {
            withContext(Dispatchers.IO) {
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

                val requestBody =
                    coordinates.toRequestBody("application/json; charset=utf-8".toMediaType())

                // Crear solicitud POST
                val request = Request.Builder()
                    .url(url)
                    .header(
                        "Authorization",
                        "5b3ce3597851110001cf6248d89338ee73144e65910e6058196bea9c"
                    ) // Usa tu API key
                    .post(requestBody)
                    .build()

                // Ejecutar la solicitud en segundo plano usando `client.newCall(request).execute()`
                try {
                    val response = client.newCall(request).execute()

                    if (response.isSuccessful) {
                        // Extraer el cuerpo de la respuesta
                        val responseBody = response.body?.string()

                        // Verificar si la respuesta no está vacía
                        if (!responseBody.isNullOrEmpty()) {
                            // Convertir la respuesta JSON en FeatureCollection
                            FeatureCollection.fromJson(responseBody)
                        } else {
                            throw IOException("Empty response body")
                        }
                    } else {
                        throw IOException("Request failed: ${response.message}")
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    throw IOException("Request failed: ${e.message}")
                }
            }
        }
    }
}