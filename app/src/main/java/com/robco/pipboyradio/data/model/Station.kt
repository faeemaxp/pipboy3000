package com.robco.pipboyradio.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Station(
    val id: String = "",
    val name: String,
    val url: String,
    val category: String = "ALL",
    val isFavorite: Boolean = false
)

enum class PipBoyTab {
    RECEIVER,
    TUNER,
    SETTINGS
}

data class RepoPreset(
    val id: String,
    val title: String,
    val description: String,
    val url: String,
    val tag: String
)

@Serializable
data class StreamMetadata(
    val title: String? = null,
    val artist: String? = null,
    val streamType: String = "HTTP / AUDIO",
    val bitrate: String = "128 KBPS",
    val frequencyDial: String = "104.2 MHz",
    val sampleRate: String = "44.1 kHz",
    val channels: String = "STEREO",
    val signalQuality: String = "98.4%",
    val serverName: String? = null
)

data class RadioUiState(
    val currentTab: PipBoyTab = PipBoyTab.RECEIVER,
    val isPlaying: Boolean = false,
    val currentStationIndex: Int = 0,
    val currentStation: Station? = null,
    val favoriteStationUrls: Set<String> = emptySet(),
    val statusSignal: String = "STANDBY",
    val temporaryStatus: String? = null,
    val searchQuery: String = "",
    val selectedCategory: String = "ALL",
    val volume: Float = 0.7f,
    val isOverdrive: Boolean = false,
    val isInterference: Boolean = false,
    val isRepoLoading: Boolean = false,
    val repoStatusMessage: String? = null,
    val totalStationCount: Int = 0,
    val searchResults: List<Station> = emptyList(),
    val isSearching: Boolean = false,
    val searchError: String? = null,
    val showRepoModal: Boolean = false,
    val showAddStationDialog: Boolean = false,
    val showMetadataDialog: Boolean = false,
    val initialAddStationName: String = "",
    val initialAddStationUrl: String = "",
    val streamMetadata: StreamMetadata = StreamMetadata(),
    // Settings toggles & options
    val autoPlayNextOnError: Boolean = true,
    val dataSaverBuffer: Boolean = false,
    val scanlinesEnabled: Boolean = true,
    val pipBoyColorTheme: String = "GREEN", // GREEN, AMBER, CYAN, WHITE
    val visualizerStyleName: String = "VFD BARS"
) {
    val displayStatus: String
        get() = temporaryStatus ?: statusSignal

    val nowPlayingDisplay: String
        get() {
            val t = streamMetadata.title?.takeIf { it.isNotBlank() }
            val a = streamMetadata.artist?.takeIf { it.isNotBlank() }
            return when {
                t != null && a != null -> "$a — $t"
                t != null -> t
                else -> currentStation?.category?.uppercase()?.let { "BROADCASTING // $it" } ?: "LIVE AUDIO BROADCAST"
            }
        }
}
