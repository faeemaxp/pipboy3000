package com.robco.pipboyradio.data.repository

import android.content.Context
import com.robco.pipboyradio.data.model.Station
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

@Serializable
private data class RadioBrowserStation(
    val stationuuid: String = "",
    val name: String = "",
    val url: String = "",
    val url_resolved: String = "",
    val tags: String = "",
    val country: String = "",
    val language: String = ""
)

class StationRepository(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private var cachedStations: List<Station> = emptyList()

    suspend fun getStations(): List<Station> = withContext(Dispatchers.IO) {
        if (cachedStations.isNotEmpty()) return@withContext cachedStations

        // By default, no stations unless user adds a repo or fetches
        cachedStations = emptyList()
        cachedStations
    }

    suspend fun loadFromRawJsonUrl(rawUrl: String): Result<List<Station>> = withContext(Dispatchers.IO) {
        try {
            val url = URL(rawUrl)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 10000
                readTimeout = 10000
                requestMethod = "GET"
                setRequestProperty("User-Agent", "PipBoy3000-Radio/1.0")
            }

            val responseCode = connection.responseCode
            if (responseCode !in 200..299) {
                return@withContext Result.failure(Exception("HTTP Error: $responseCode"))
            }

            val body = connection.inputStream.bufferedReader().use { it.readText() }
            val stations: List<Station> = json.decodeFromString(body)
            cachedStations = stations
            Result.success(stations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loadFromRadioBrowser(
        filterType: String,
        filterValue: String,
        limit: Int = 30
    ): Result<List<Station>> = withContext(Dispatchers.IO) {
        try {
            val sanitizedValue = java.net.URLEncoder.encode(filterValue.trim(), "UTF-8")
            val endpoint = when (filterType.lowercase()) {
                "country" -> "https://de1.api.radio-browser.info/json/stations/bycountry/$sanitizedValue?limit=$limit"
                "language" -> "https://de1.api.radio-browser.info/json/stations/bylanguage/$sanitizedValue?limit=$limit"
                "tag" -> "https://de1.api.radio-browser.info/json/stations/bytag/$sanitizedValue?limit=$limit"
                else -> "https://de1.api.radio-browser.info/json/stations/bytag/$sanitizedValue?limit=$limit"
            }

            val url = URL(endpoint)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 10000
                readTimeout = 10000
                requestMethod = "GET"
                setRequestProperty("User-Agent", "PipBoy3000-Radio/1.0")
            }

            val responseCode = connection.responseCode
            if (responseCode !in 200..299) {
                return@withContext Result.failure(Exception("HTTP $responseCode from Radio-Browser"))
            }

            val body = connection.inputStream.bufferedReader().use { it.readText() }
            val rbStations: List<RadioBrowserStation> = json.decodeFromString(body)

            val stations = rbStations.mapNotNull { rb ->
                val streamUrl = rb.url_resolved.ifBlank { rb.url }
                if (streamUrl.isBlank()) return@mapNotNull null
                val cat = when {
                    rb.tags.contains("pop", ignoreCase = true) -> "POP"
                    rb.tags.contains("rock", ignoreCase = true) -> "ROCK"
                    rb.tags.contains("jazz", ignoreCase = true) -> "JAZZ"
                    rb.tags.contains("classical", ignoreCase = true) -> "CLASSICAL"
                    rb.tags.contains("ambient", ignoreCase = true) -> "AMBIENT"
                    rb.country.isNotBlank() -> rb.country.uppercase()
                    else -> "BROADCAST"
                }
                Station(
                    id = rb.stationuuid.ifBlank { streamUrl },
                    name = rb.name.trim().ifBlank { "Unknown Signal" },
                    url = streamUrl,
                    category = cat
                )
            }

            cachedStations = stations
            Result.success(stations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loadBundledAssetStations(): List<Station> = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("stations.json").bufferedReader().use { it.readText() }
            val stations: List<Station> = json.decodeFromString(jsonString)
            cachedStations = stations
            stations
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun clearStations(): List<Station> {
        cachedStations = emptyList()
        return emptyList()
    }
}
