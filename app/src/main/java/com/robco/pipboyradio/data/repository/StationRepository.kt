package com.robco.pipboyradio.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.robco.pipboyradio.data.model.Station
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

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
        encodeDefaults = true
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences("pipboy_radio_prefs", Context.MODE_PRIVATE)

    private val PREF_KEY_STATIONS = "cached_stations_json"

    private var cachedStations: MutableList<Station> = mutableListOf()

    suspend fun getStations(): List<Station> = withContext(Dispatchers.IO) {
        if (cachedStations.isNotEmpty()) {
            return@withContext cachedStations.toList()
        }

        // Try to load from persistent storage
        val savedJson = prefs.getString(PREF_KEY_STATIONS, null)
        if (!savedJson.isNullOrBlank()) {
            try {
                val loaded: List<Station> = json.decodeFromString(savedJson)
                cachedStations = loaded.distinctBy { it.url }.toMutableList()
                return@withContext cachedStations.toList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Fallback to bundled asset stations
        val bundled = loadBundledAssetStations(append = false)
        cachedStations = bundled.distinctBy { it.url }.toMutableList()
        persistStations(cachedStations)
        cachedStations.toList()
    }

    fun addStation(station: Station): List<Station> {
        val existingIndex = cachedStations.indexOfFirst { it.url == station.url }
        if (existingIndex < 0) {
            // Add custom station to the beginning of the list for instant visibility
            cachedStations.add(0, station)
        } else {
            cachedStations[existingIndex] = station
        }
        persistStations(cachedStations)
        return cachedStations.toList()
    }

    fun addStations(newStations: List<Station>): List<Station> {
        val existingUrls = cachedStations.map { it.url }.toSet()
        val toAdd = newStations.distinctBy { it.url }.filter { it.url !in existingUrls }
        cachedStations.addAll(toAdd)
        persistStations(cachedStations)
        return cachedStations.toList()
    }

    fun removeStation(stationUrl: String): List<Station> {
        cachedStations.removeAll { it.url == stationUrl }
        persistStations(cachedStations)
        return cachedStations.toList()
    }

    fun removeStations(stationUrls: Set<String>): List<Station> {
        cachedStations.removeAll { it.url in stationUrls }
        persistStations(cachedStations)
        return cachedStations.toList()
    }

    fun clearStations(): List<Station> {
        cachedStations.clear()
        persistStations(cachedStations)
        return emptyList()
    }

    suspend fun restoreDefaultStations(): List<Station> = withContext(Dispatchers.IO) {
        val defaults = loadBundledAssetStations(append = false)
        cachedStations = defaults.toMutableList()
        persistStations(cachedStations)
        cachedStations.toList()
    }

    private fun persistStations(stations: List<Station>) {
        try {
            val serialized = json.encodeToString(stations)
            prefs.edit().putString(PREF_KEY_STATIONS, serialized).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun loadFromRawJsonUrl(rawUrl: String, append: Boolean = false): Result<List<Station>> = withContext(Dispatchers.IO) {
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
            val stations: List<Station> = json.decodeFromString<List<Station>>(body).distinctBy { it.url }

            if (append) {
                addStations(stations)
            } else {
                cachedStations = stations.toMutableList()
                persistStations(cachedStations)
            }
            Result.success(cachedStations.toList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchRadioBrowser(
        query: String,
        filterType: String = "name",
        limit: Int = 30
    ): Result<List<Station>> = withContext(Dispatchers.IO) {
        try {
            val trimmed = query.trim()
            if (trimmed.isBlank()) {
                return@withContext Result.success(emptyList())
            }

            val sanitizedValue = URLEncoder.encode(trimmed, "UTF-8")
            val endpoint = when (filterType.lowercase()) {
                "name", "byname" -> "https://de1.api.radio-browser.info/json/stations/byname/$sanitizedValue?limit=$limit"
                "country", "bycountry" -> "https://de1.api.radio-browser.info/json/stations/bycountry/$sanitizedValue?limit=$limit"
                "language", "bylanguage" -> "https://de1.api.radio-browser.info/json/stations/bylanguage/$sanitizedValue?limit=$limit"
                "tag", "bytag" -> "https://de1.api.radio-browser.info/json/stations/bytag/$sanitizedValue?limit=$limit"
                else -> "https://de1.api.radio-browser.info/json/stations/byname/$sanitizedValue?limit=$limit"
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
                return@withContext Result.failure(Exception("Radio-Browser returned HTTP $responseCode"))
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
            }.distinctBy { it.url }

            Result.success(stations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loadBundledAssetStations(append: Boolean = false): List<Station> = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("stations.json").bufferedReader().use { it.readText() }
            val stations: List<Station> = json.decodeFromString<List<Station>>(jsonString).distinctBy { it.url }
            if (append) {
                addStations(stations)
            } else {
                cachedStations = stations.toMutableList()
                persistStations(cachedStations)
            }
            cachedStations.toList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
