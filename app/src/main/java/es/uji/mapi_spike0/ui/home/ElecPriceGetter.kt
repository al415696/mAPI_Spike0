package es.uji.mapi_spike0.ui.home

import android.view.View
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import com.mapbox.geojson.Point
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.lang.reflect.Type
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin


class ElecPriceGetter {

    // Modelo de datos para deserializar la respuesta JSON

    data class Elec(
        @SerializedName("date") val date: String,
        @SerializedName("market") val market: String,
        @SerializedName("price") val price: Double,
        @SerializedName("units") val units: String

    )

    // simple deserializer that always returns object with value x = 4444
    class ElecDeserializer : JsonDeserializer<Elec> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): Elec? {

            var jsonObject = json?.asJsonObject

            if (jsonObject != null) {
                val date: String = jsonObject.get("date").asString
                val market: String = jsonObject.get("market").asString
                val price: Double = jsonObject.get("price").asString.replace(",", ".").toDouble()
                val units: String = jsonObject.get("units").asString
                return Elec(
                    date,
                    market,
                    price,
                    units
                )

            }
            println("error con adapter")
            return null
        }
    }

    // Función suspendida para realizar la petición HTTP y obtener los datos
    suspend fun obtenerPrecioMedioElec(): Elec? {
        return withContext(Dispatchers.IO) {  // Ejecutar en el hilo de entrada/salida
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://api.preciodelaluz.org/v1/prices/avg?zone=PCB") // Precio elec medio
                .build()

            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        var builder = GsonBuilder()
                        builder =
                            builder.registerTypeAdapter(
                                Elec::class.java,
                                ElecDeserializer()
                            )
                        val gson = builder.create()
                        val data = gson.fromJson(responseBody, Elec::class.java)

                        // Filtrar solo las gasolineras de Madrid
                        println("Todo obtenido")
                        return@withContext data//.filter { it.localidad.contains("Madrid") }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            println("Nada obtenido")
            return@withContext null  // En caso de error o fallo en la solicitud
        }
    }
}
