package es.uji.mapi_spike0.ui.home

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


class PriceGetter {

    // Modelo de datos para deserializar la respuesta JSON

    data class Gasolinera(
        @SerializedName("C.P.") val codigoPostal: String,
        @SerializedName("Dirección") val direccion: String,
        @SerializedName("Horario") val horario: String,
        @SerializedName("Latitud") val latitud: Float,
//        @SerializedName("Latitud") val latitud: String,
        @SerializedName("Localidad") val localidad: String,
        @SerializedName("Longitud (WGS84)") val longitudWGS84: Float,
//        @SerializedName("Longitud (WGS84)") val longitudWGS84: String,
        @SerializedName("Margen") val margen: String,
        @SerializedName("Municipio") val municipio: String,
        @SerializedName("PrecioProducto") val precioProducto: Float,
        @SerializedName("Provincia") val provincia: String,
        @SerializedName("Remisión") val remision: String,
        @SerializedName("Rótulo") val rotulo: String,
        @SerializedName("Tipo Venta") val tipoVenta: String,
        @SerializedName("IDEESS") val ideess: String,
        @SerializedName("IDMunicipio") val idMunicipio: String,
        @SerializedName("IDProvincia") val idProvincia: String,
        @SerializedName("IDCCAA") val idCCAA: String
    )

    // simple deserializer that always returns object with value x = 4444
    class GasolineraDeserializer : JsonDeserializer<Gasolinera> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): Gasolinera? {

            var jsonObject = json?.asJsonObject

            if (jsonObject != null) {
                val codigoPostal: String = jsonObject.get("C.P.").asString
                val direccion: String = jsonObject.get("Dirección").asString
                val horario: String = jsonObject.get("Horario").asString
                val latitud: Float = jsonObject.get("Latitud").asString.replace(",", ".").toFloat()
                val localidad: String = jsonObject.get("Localidad").asString
                val longitudWGS84: Float =
                    jsonObject.get("Longitud (WGS84)").asString.replace(",", ".").toFloat()
                val margen: String = jsonObject.get("Margen").asString
                val municipio: String = jsonObject.get("Municipio").asString
                val precioProducto: Float =
                    jsonObject.get("PrecioProducto").asString.replace(",", ".").toFloat()
                val provincia: String = jsonObject.get("Provincia").asString
                val remision: String = jsonObject.get("Remisión").asString
                val rotulo: String = jsonObject.get("Rótulo").asString
                val tipoVenta: String = jsonObject.get("Tipo Venta").asString
                val ideess: String = jsonObject.get("IDEESS").asString
                val idMunicipio: String = jsonObject.get("IDMunicipio").asString
                val idProvincia: String = jsonObject.get("IDProvincia").asString
                val idCCAA: String = jsonObject.get("IDCCAA").asString
                println("Perfectérrimo")

                return Gasolinera(
                    codigoPostal,
                    direccion,
                    horario,
                    latitud,
                    localidad,
                    longitudWGS84,
                    margen,
                    municipio,
                    precioProducto,
                    provincia,
                    remision,
                    rotulo,
                    tipoVenta,
                    ideess,
                    idMunicipio,
                    idProvincia,
                    idCCAA
                )

            }
            println("error con adapter")
            return null
        }
    }

    data class ResponseData(
//        @SerializedName("ListaEESSPrecio") val gasolineras: List<Gasolinera>
        @SerializedName("Fecha") val fecha: String,
        @SerializedName("ListaEESSPrecio") val gasolineras: List<Gasolinera>,
//        @SerializedName("Nota") val nota: String,
//        @SerializedName("ResultadoConsulta") val resultadoConsulta: String
    )

    class GasolinerasManager {
        var gasolineras: List<Gasolinera> = listOf()

        constructor(gasolineras: List<Gasolinera>){
            this.gasolineras = gasolineras
        }

        public fun getClosest(location: Point): Gasolinera {
            var closest: Gasolinera = gasolineras.get(0)

            for (gas in gasolineras) {
                if (distance(
                        gas.latitud.toDouble(),
                        gas.longitudWGS84.toDouble(),
                        location.latitude(),
                        location.longitude()
                    ) < distance(
                        gas.latitud.toDouble(),
                        gas.longitudWGS84.toDouble(),
                        location.latitude(),
                        location.longitude()
                    )
                )
                    closest = gas
            }
            return closest
        }

        private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val theta = lon1 - lon2
            var dist = sin(deg2rad(lat1)) * sin(deg2rad(lat2)) + cos(
                deg2rad(lat1)
            ) * cos(deg2rad(lat2)) * cos(deg2rad(theta))
            dist = acos(dist)
            dist = rad2deg(dist)
            dist = dist * 60 * 1.1515
            return (dist)
        }

        private fun deg2rad(deg: Double): Double {
            return (deg * Math.PI / 180.0)
        }

        private fun rad2deg(rad: Double): Double {
            return (rad * 180.0 / Math.PI)
        }
    }

    // Función suspendida para realizar la petición HTTP y obtener los datos
    suspend fun obtenerPreciosCarburantesMadrid(): List<Gasolinera>? {
        return withContext(Dispatchers.IO) {  // Ejecutar en el hilo de entrada/salida
            val client = OkHttpClient()
            val request = Request.Builder()
//                .url("https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/")
                .url("https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/FiltroCCAAProducto/10/1") // Gasoleo A, en Comunidad Valenciana
                .build()

            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        println("Todo JSON: " + responseBody)
                        var builder = GsonBuilder()
                        builder =
                            builder.registerTypeAdapter(
                                Gasolinera::class.java,
                                GasolineraDeserializer()
                            )
                        val gson = builder.create()
//                        val gson = Gson()
                        val data = gson.fromJson(responseBody, ResponseData::class.java)

                        // Filtrar solo las gasolineras de Madrid
                        println("Todo obtenido")
                        return@withContext data.gasolineras//.filter { it.localidad.contains("Madrid") }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            println("Nada obtenido")
            return@withContext null  // En caso de error o fallo en la solicitud
        }
    }

    // Función para iniciar la solicitud y manejar los datos obtenidos
    fun iniciarSolicitud() {
        CoroutineScope(Dispatchers.Main).launch {
            val gasolineras = obtenerPreciosCarburantesMadrid()
            println("Lo obtenido es " + gasolineras)
            if (gasolineras != null) {
                // Mostrar los precios de las gasolineras en Madrid
                gasolineras.forEach { gasolinera ->
                    println("Localidad: ${gasolinera.localidad}")
                    println("Precio Gasolina 95: ${gasolinera.precioProducto ?: "No disponible"}")
                    println("-------------------------------")
                }
            } else {
                println("Error obteniendo los datos")
            }
        }
    }
}
