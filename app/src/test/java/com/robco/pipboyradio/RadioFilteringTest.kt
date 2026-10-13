package com.robco.pipboyradio

import com.robco.pipboyradio.data.model.Station
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RadioFilteringTest {

    private val sampleStations = listOf(
        Station(id = "1", name = "SomaFM: Groove Salad", url = "https://example.com/1", category = "Wasteland Ambient"),
        Station(id = "2", name = "SomaFM: Secret Agent", url = "https://example.com/2", category = "Vault Jazz"),
        Station(id = "3", name = "Nightwave Plaza", url = "https://example.com/3", category = "Pre-War Wave"),
        Station(id = "4", name = "Vividh Bharati", url = "https://example.com/4", category = "Vividh Bharati"),
        Station(id = "5", name = "FM Gold Delhi", url = "https://example.com/5", category = "FM"),
        Station(id = "6", name = "Akashvani Mumbai", url = "https://example.com/6", category = "Akashvani")
    )

    private fun filter(stations: List<Station>, query: String, category: String): List<Station> {
        val q = query.trim().lowercase()
        return stations.filter { station ->
            val matchesQuery = q.isEmpty() || station.name.lowercase().contains(q)
            if (!matchesQuery) return@filter false

            when (category) {
                "ALL" -> true
                "WASTELAND" -> station.category.contains("Wasteland", ignoreCase = true) ||
                        station.category.contains("Vault", ignoreCase = true) ||
                        station.category.contains("Pre-War", ignoreCase = true)
                "VIVIDH BHARATI" -> station.name.contains("Vividh", ignoreCase = true) ||
                        station.name.contains("VBS", ignoreCase = true) ||
                        station.category.equals("Vividh Bharati", ignoreCase = true)
                "FM" -> station.name.contains("FM", ignoreCase = true)
                "AKASHVANI" -> station.name.contains("Akashvani", ignoreCase = true)
                else -> true
            }
        }
    }

    @Test
    fun testCategoryFiltering() {
        val all = filter(sampleStations, "", "ALL")
        assertEquals(6, all.size)

        val wasteland = filter(sampleStations, "", "WASTELAND")
        assertEquals(3, wasteland.size)

        val fm = filter(sampleStations, "", "FM")
        assertEquals(1, fm.size)
        assertEquals("FM Gold Delhi", fm[0].name)

        val akashvani = filter(sampleStations, "", "AKASHVANI")
        assertEquals(1, akashvani.size)
        assertEquals("Akashvani Mumbai", akashvani[0].name)
    }

    @Test
    fun testSearchQueryFiltering() {
        val searchJazz = filter(sampleStations, "Secret", "ALL")
        assertEquals(1, searchJazz.size)
        assertEquals("SomaFM: Secret Agent", searchJazz[0].name)

        val caseInsensitive = filter(sampleStations, "delhi", "ALL")
        assertEquals(1, caseInsensitive.size)
        assertEquals("FM Gold Delhi", caseInsensitive[0].name)
    }

    @Test
    fun testNextPrevCycling() {
        val total = 5
        var current = 0

        // Next
        current = (current + 1) % total
        assertEquals(1, current)

        // Prev from 0 wraps to end
        var start = 0
        val wrappedPrev = (start - 1 + total) % total
        assertEquals(4, wrappedPrev)
    }

    @Test
    fun testVolumeClamping() {
        fun clampVolume(vol: Float): Float = vol.coerceIn(0f, 1f)

        assertEquals(0f, clampVolume(-0.5f), 0.001f)
        assertEquals(1f, clampVolume(1.5f), 0.001f)
        assertEquals(0.65f, clampVolume(0.65f), 0.001f)
    }
}
