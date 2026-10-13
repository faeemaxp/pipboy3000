package com.robco.pipboyradio

import com.robco.pipboyradio.data.model.RadioUiState
import com.robco.pipboyradio.data.model.Station
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StationModelTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testStationSerialization() {
        val jsonString = """
            {
                "id": "test-station",
                "name": "Radio New Vegas",
                "url": "https://example.com/stream.mp3",
                "category": "Wasteland Ambient"
            }
        """.trimIndent()

        val station = json.decodeFromString<Station>(jsonString)
        assertEquals("test-station", station.id)
        assertEquals("Radio New Vegas", station.name)
        assertEquals("https://example.com/stream.mp3", station.url)
        assertEquals("Wasteland Ambient", station.category)
        assertFalse(station.isFavorite)
    }

    @Test
    fun testRadioUiStateDisplayStatus() {
        val defaultState = RadioUiState()
        assertEquals("STANDBY", defaultState.displayStatus)
        assertNull(defaultState.temporaryStatus)
        assertEquals(0.7f, defaultState.volume, 0.01f)
        assertFalse(defaultState.isOverdrive)

        val alertState = defaultState.copy(temporaryStatus = "!!! OVERDRIVE !!!")
        assertEquals("!!! OVERDRIVE !!!", alertState.displayStatus)

        val customStatusState = defaultState.copy(
            statusSignal = "BROADCASTING",
            temporaryStatus = null
        )
        assertEquals("BROADCASTING", customStatusState.displayStatus)
    }

    @Test
    fun testStationListParsing() {
        val listJson = """
            [
                {"id": "1", "name": "SomaFM", "url": "https://ice1.somafm.com/groovesalad-128-mp3", "category": "Ambient"},
                {"id": "2", "name": "Vividh Bharati", "url": "https://example.com/vb.m3u8", "category": "AIR"}
            ]
        """.trimIndent()

        val list = json.decodeFromString<List<Station>>(listJson)
        assertEquals(2, list.size)
        assertEquals("SomaFM", list[0].name)
        assertEquals("Vividh Bharati", list[1].name)
    }

    @Test
    fun testTabAndFavorites() {
        val state = RadioUiState(
            currentTab = com.robco.pipboyradio.data.model.PipBoyTab.TUNER,
            favoriteStationUrls = setOf("https://example.com/stream.mp3")
        )
        assertEquals(com.robco.pipboyradio.data.model.PipBoyTab.TUNER, state.currentTab)
        assertTrue(state.favoriteStationUrls.contains("https://example.com/stream.mp3"))
    }
}
