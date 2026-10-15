package com.robco.pipboyradio.ui.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.robco.pipboyradio.data.model.PipBoyTab
import com.robco.pipboyradio.data.model.RadioUiState
import com.robco.pipboyradio.data.model.Station
import com.robco.pipboyradio.data.model.StreamMetadata
import com.robco.pipboyradio.data.repository.StationRepository
import com.robco.pipboyradio.service.RadioPlaybackService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.absoluteValue

class RadioViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StationRepository(application)
    private val prefs = application.getSharedPreferences("pipboy_settings_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(
        RadioUiState(
            autoPlayNextOnError = prefs.getBoolean("pref_auto_play_next", true),
            dataSaverBuffer = prefs.getBoolean("pref_data_saver", false),
            scanlinesEnabled = prefs.getBoolean("pref_scanlines", true),
            pipBoyColorTheme = prefs.getString("pref_color_theme", "GREEN") ?: "GREEN",
            visualizerStyleName = prefs.getString("pref_vis_style", "VFD BARS") ?: "VFD BARS"
        )
    )
    val uiState: StateFlow<RadioUiState> = _uiState.asStateFlow()

    private var allStations: List<Station> = emptyList()
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null

    private var tempStatusJob: Job? = null

    init {
        loadStations()
        initMediaController()
    }

    private fun loadStations() {
        viewModelScope.launch {
            allStations = repository.getStations()
            val first = allStations.firstOrNull()
            _uiState.update {
                it.copy(
                    totalStationCount = allStations.size,
                    currentStation = first,
                    currentStationIndex = 0,
                    streamMetadata = createInitialMetadata(first)
                )
            }
        }
    }

    private fun createInitialMetadata(station: Station?): StreamMetadata {
        if (station == null) return StreamMetadata()
        val freqMhz = 88.0f + (station.name.hashCode().absoluteValue % 200) / 10.0f
        val freqStr = String.format(Locale.US, "%.1f MHz", freqMhz)
        val streamType = when {
            station.url.contains(".m3u8", ignoreCase = true) || station.url.contains("hls", ignoreCase = true) -> "HLS / AAC AUDIO"
            station.url.contains("mp3", ignoreCase = true) -> "ICECAST / MP3 AUDIO"
            station.url.contains("aac", ignoreCase = true) -> "AAC STREAM (BROADBAND)"
            else -> "HTTP AUDIO RELAY"
        }
        return StreamMetadata(
            title = null,
            artist = null,
            streamType = streamType,
            bitrate = if (streamType.contains("AAC")) "96 KBPS" else "128 KBPS",
            frequencyDial = freqStr,
            sampleRate = "44.1 kHz",
            channels = "STEREO",
            signalQuality = "98.4%"
        )
    }

    // Modal dialog controls
    fun setShowRepoModal(show: Boolean) {
        _uiState.update { it.copy(showRepoModal = show) }
    }

    fun openAddStationDialog(initialName: String = "", initialUrl: String = "") {
        _uiState.update {
            it.copy(
                showAddStationDialog = true,
                initialAddStationName = initialName,
                initialAddStationUrl = initialUrl
            )
        }
    }

    fun closeAddStationDialog() {
        _uiState.update {
            it.copy(
                showAddStationDialog = false,
                initialAddStationName = "",
                initialAddStationUrl = ""
            )
        }
    }

    fun openMetadataDialog() {
        _uiState.update { it.copy(showMetadataDialog = true) }
    }

    fun closeMetadataDialog() {
        _uiState.update { it.copy(showMetadataDialog = false) }
    }

    // Online search through Radio-Browser satellite directory
    fun searchOnlineStations(query: String, filterType: String = "name") {
        val trimmed = query.trim()
        if (trimmed.isBlank()) {
            _uiState.update { it.copy(searchResults = emptyList(), searchError = null, isSearching = false) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true, searchError = null) }
            val result = repository.searchRadioBrowser(trimmed, filterType, limit = 40)
            result.onSuccess { stations ->
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        searchResults = stations,
                        searchError = if (stations.isEmpty()) "NO SIGNALS FOUND FOR \"$trimmed\"" else null
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        searchResults = emptyList(),
                        searchError = "SEARCH ERROR: ${err.message ?: "UNABLE TO REACH SATELLITE RELAY"}"
                    )
                }
            }
        }
    }

    fun clearSearchResults() {
        _uiState.update { it.copy(searchResults = emptyList(), searchError = null, isSearching = false) }
    }

    // Station Management
    fun addStationToTuner(station: Station) {
        allStations = repository.addStation(station)
        _uiState.update {
            it.copy(
                totalStationCount = allStations.size,
                currentStation = it.currentStation ?: station
            )
        }
        showTemporaryStatus("ADDED: ${station.name}", 2500)
    }

    fun addAllSearchResultsToTuner() {
        val results = _uiState.value.searchResults
        if (results.isEmpty()) return
        allStations = repository.addStations(results)
        _uiState.update {
            it.copy(
                totalStationCount = allStations.size,
                currentStation = it.currentStation ?: allStations.firstOrNull()
            )
        }
        showTemporaryStatus("ADDED ${results.size} STATIONS TO TUNER", 3000)
    }

    fun removeStationFromTuner(station: Station) {
        val wasCurrent = _uiState.value.currentStation?.url == station.url
        allStations = repository.removeStation(station.url)

        if (wasCurrent) {
            mediaController?.stop()
        }

        _uiState.update {
            it.copy(
                totalStationCount = allStations.size,
                currentStation = if (wasCurrent) allStations.firstOrNull() else it.currentStation,
                isPlaying = if (wasCurrent) false else it.isPlaying,
                statusSignal = if (wasCurrent) "STANDBY" else it.statusSignal
            )
        }
        showTemporaryStatus("REMOVED: ${station.name}", 2500)
    }

    fun removeStationsFromTuner(stationUrls: Set<String>) {
        if (stationUrls.isEmpty()) return
        val currentUrl = _uiState.value.currentStation?.url
        val wasCurrent = currentUrl != null && currentUrl in stationUrls
        allStations = repository.removeStations(stationUrls)

        if (wasCurrent) {
            mediaController?.stop()
        }

        _uiState.update {
            it.copy(
                totalStationCount = allStations.size,
                currentStation = if (wasCurrent) allStations.firstOrNull() else it.currentStation,
                isPlaying = if (wasCurrent) false else it.isPlaying,
                statusSignal = if (wasCurrent) "STANDBY" else it.statusSignal
            )
        }
        showTemporaryStatus("REMOVED ${stationUrls.size} FREQUENCIES", 2500)
    }

    fun clearAllStations() {
        allStations = repository.clearStations()
        mediaController?.stop()
        _uiState.update {
            it.copy(
                totalStationCount = 0,
                currentStation = null,
                currentStationIndex = 0,
                isPlaying = false,
                statusSignal = "STANDBY",
                repoStatusMessage = "ALL STATIONS PURGED (EMPTY LIBRARY)",
                streamMetadata = StreamMetadata()
            )
        }
        showTemporaryStatus("ALL STATIONS PURGED", 3000)
    }

    fun restoreDefaultStations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRepoLoading = true, repoStatusMessage = "RESTORING 275+ VAULT DEFAULTS...") }
            allStations = repository.restoreDefaultStations()
            val first = allStations.firstOrNull()
            _uiState.update {
                it.copy(
                    isRepoLoading = false,
                    totalStationCount = allStations.size,
                    currentStation = first,
                    currentStationIndex = 0,
                    repoStatusMessage = "RESTORED ${allStations.size} ROBCO DEFAULT STATIONS",
                    streamMetadata = createInitialMetadata(first)
                )
            }
            showTemporaryStatus("RESTORED ${allStations.size} DEFAULT FREQUENCIES", 3500)
        }
    }

    fun loadFromRawJsonUrl(url: String, append: Boolean = false) {
        if (url.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isRepoLoading = true, repoStatusMessage = "SYNCING REPO FROM GITHUB...") }
            val result = repository.loadFromRawJsonUrl(url.trim(), append = append)
            result.onSuccess { stations ->
                allStations = stations
                val current = _uiState.value.currentStation ?: stations.firstOrNull()
                _uiState.update {
                    it.copy(
                        isRepoLoading = false,
                        totalStationCount = stations.size,
                        currentStation = current,
                        currentStationIndex = 0,
                        repoStatusMessage = "SUCCESS: ${if (append) "MERGED" else "LOADED"} ${stations.size} STATIONS",
                        streamMetadata = createInitialMetadata(current)
                    )
                }
                showTemporaryStatus("REPO SYNCED: ${stations.size} FREQUENCIES ACTIVE", 4000)
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isRepoLoading = false,
                        repoStatusMessage = "ERROR: ${err.message ?: "FAILED TO FETCH REPO"}"
                    )
                }
                showTemporaryStatus("REPO ERROR: FAILED TO PARSE JSON", 4000)
            }
        }
    }

    fun loadBundledStations(append: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRepoLoading = true, repoStatusMessage = "LOADING PRE-CONFIGURED STATIONS...") }
            val stations = repository.loadBundledAssetStations(append = append)
            allStations = stations
            val current = _uiState.value.currentStation ?: stations.firstOrNull()
            _uiState.update {
                it.copy(
                    isRepoLoading = false,
                    totalStationCount = stations.size,
                    currentStation = current,
                    currentStationIndex = 0,
                    repoStatusMessage = "${if (append) "MERGED" else "LOADED"} ${stations.size} VAULT STATIONS",
                    streamMetadata = createInitialMetadata(current)
                )
            }
            showTemporaryStatus("VAULT ARCHIVE: ${stations.size} STATIONS ACTIVE", 3500)
        }
    }

    // Settings Toggles
    fun toggleAutoPlayNextOnError() {
        val newVal = !_uiState.value.autoPlayNextOnError
        _uiState.update { it.copy(autoPlayNextOnError = newVal) }
        prefs.edit().putBoolean("pref_auto_play_next", newVal).apply()
        showTemporaryStatus("AUTO-ADVANCE ON ERROR: ${if (newVal) "ON" else "OFF"}", 2000)
    }

    fun toggleDataSaver() {
        val newVal = !_uiState.value.dataSaverBuffer
        _uiState.update { it.copy(dataSaverBuffer = newVal) }
        prefs.edit().putBoolean("pref_data_saver", newVal).apply()
        showTemporaryStatus("DATA SAVER BUFFER: ${if (newVal) "ENABLED" else "DISABLED"}", 2000)
    }

    fun toggleScanlines() {
        val newVal = !_uiState.value.scanlinesEnabled
        _uiState.update { it.copy(scanlinesEnabled = newVal) }
        prefs.edit().putBoolean("pref_scanlines", newVal).apply()
        showTemporaryStatus("CRT SCANLINES: ${if (newVal) "ENABLED" else "DISABLED"}", 2000)
    }

    fun setColorTheme(themeName: String) {
        _uiState.update { it.copy(pipBoyColorTheme = themeName) }
        prefs.edit().putString("pref_color_theme", themeName).apply()
        showTemporaryStatus("COLOR THEME: $themeName", 2000)
    }

    fun setVisualizerStyle(styleName: String) {
        _uiState.update { it.copy(visualizerStyleName = styleName) }
        prefs.edit().putString("pref_vis_style", styleName).apply()
        showTemporaryStatus("VISUALIZER STYLE: $styleName", 2000)
    }

    fun setTab(tab: PipBoyTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun toggleFavorite(station: Station) {
        _uiState.update { state ->
            val isFav = state.favoriteStationUrls.contains(station.url)
            val updated = if (isFav) {
                state.favoriteStationUrls - station.url
            } else {
                state.favoriteStationUrls + station.url
            }
            state.copy(favoriteStationUrls = updated)
        }
    }

    fun getFilteredStations(): List<Station> {
        val query = _uiState.value.searchQuery.trim().lowercase()
        val cat = _uiState.value.selectedCategory
        val favs = _uiState.value.favoriteStationUrls

        return allStations.distinctBy { it.url }.filter { station ->
            val matchesQuery = query.isEmpty() ||
                    station.name.lowercase().contains(query) ||
                    station.category.lowercase().contains(query) ||
                    station.url.lowercase().contains(query)
            if (!matchesQuery) return@filter false

            when (cat) {
                "ALL" -> true
                "FAVORITES" -> favs.contains(station.url)
                "WASTELAND" -> station.category.contains("Wasteland", ignoreCase = true) ||
                        station.category.contains("Vault", ignoreCase = true) ||
                        station.category.contains("Pre-War", ignoreCase = true)
                "VIVIDH BHARATI" -> station.name.contains("Vividh", ignoreCase = true) ||
                        station.name.contains("VBS", ignoreCase = true) ||
                        station.category.equals("Vividh Bharati", ignoreCase = true)
                "FM" -> station.name.contains("FM", ignoreCase = true) ||
                        station.category.contains("FM", ignoreCase = true)
                "AKASHVANI" -> station.name.contains("Akashvani", ignoreCase = true) ||
                        station.category.contains("Akashvani", ignoreCase = true)
                "JAZZ" -> station.name.contains("Jazz", ignoreCase = true) ||
                        station.category.contains("Jazz", ignoreCase = true)
                "CLASSICAL" -> station.name.contains("Classical", ignoreCase = true) ||
                        station.category.contains("Classical", ignoreCase = true)
                "AMBIENT" -> station.name.contains("Ambient", ignoreCase = true) ||
                        station.category.contains("Ambient", ignoreCase = true)
                else -> true
            }
        }.map { station ->
            station.copy(isFavorite = favs.contains(station.url))
        }
    }

    @OptIn(UnstableApi::class)
    private fun initMediaController() {
        val context = getApplication<Application>()
        val sessionToken = SessionToken(context, ComponentName(context, RadioPlaybackService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture?.addListener({
            try {
                mediaController = controllerFuture?.get()
                mediaController?.addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _uiState.update { it.copy(isPlaying = isPlaying) }
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_BUFFERING -> {
                                _uiState.update { it.copy(statusSignal = "ACQUIRING...") }
                            }
                            Player.STATE_READY -> {
                                if (mediaController?.isPlaying == true) {
                                    _uiState.update { it.copy(statusSignal = "BROADCASTING") }
                                }
                            }
                            Player.STATE_IDLE -> {
                                _uiState.update { it.copy(statusSignal = "STANDBY") }
                            }
                            Player.STATE_ENDED -> {
                                _uiState.update { it.copy(statusSignal = "STANDBY") }
                            }
                        }
                    }

                    // Live ICY metadata extraction from radio broadcast
                    override fun onMediaMetadataChanged(mediaMetadata: androidx.media3.common.MediaMetadata) {
                        val title = mediaMetadata.title?.toString()?.trim()?.takeIf { it.isNotBlank() }
                        val artist = mediaMetadata.artist?.toString()?.trim()?.takeIf { it.isNotBlank() }
                        if (title != null || artist != null) {
                            _uiState.update { state ->
                                state.copy(
                                    streamMetadata = state.streamMetadata.copy(
                                        title = title ?: state.streamMetadata.title,
                                        artist = artist ?: state.streamMetadata.artist
                                    )
                                )
                            }
                        }
                    }

                    override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                        _uiState.update { it.copy(statusSignal = "SIGNAL ERR") }
                        if (_uiState.value.autoPlayNextOnError && allStations.size > 1) {
                            showTemporaryStatus("SIGNAL LOST — SCANNING NEXT FREQUENCY...", 3000)
                            viewModelScope.launch {
                                delay(2000)
                                nextStation()
                            }
                        }
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, MoreExecutors.directExecutor())
    }

    fun playStation(station: Station) {
        val idx = allStations.indexOfFirst { it.url == station.url }
        if (idx >= 0) {
            playStation(idx)
        }
    }

    fun playStation(index: Int) {
        if (index < 0 || index >= allStations.size) return
        val station = allStations[index]

        val initialMeta = createInitialMetadata(station)

        _uiState.update {
            it.copy(
                currentStationIndex = index,
                currentStation = station,
                isPlaying = true,
                statusSignal = "ACQUIRING...",
                streamMetadata = initialMeta
            )
        }

        mediaController?.let { player ->
            player.stop()
            player.clearMediaItems()
            val mediaItem = MediaItem.fromUri(station.url)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        }
    }

    fun togglePower() {
        if (_uiState.value.isPlaying) {
            mediaController?.pause()
            _uiState.update {
                it.copy(
                    isPlaying = false,
                    statusSignal = "STANDBY"
                )
            }
        } else {
            playStation(_uiState.value.currentStationIndex)
        }
    }

    fun prevStation() {
        val total = allStations.size
        if (total == 0) return
        val newIdx = (_uiState.value.currentStationIndex - 1 + total) % total
        playStation(newIdx)
    }

    fun nextStation() {
        val total = allStations.size
        if (total == 0) return
        val newIdx = (_uiState.value.currentStationIndex + 1) % total
        playStation(newIdx)
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun setVolume(vol: Float) {
        val clamped = vol.coerceIn(0f, 1f)
        _uiState.update { it.copy(volume = clamped) }
        mediaController?.volume = clamped
    }

    fun toggleOverdrive() {
        val isNowOverdrive = !_uiState.value.isOverdrive
        _uiState.update { it.copy(isOverdrive = isNowOverdrive) }
        if (isNowOverdrive) {
            showTemporaryStatus("!!! OVERDRIVE BOOST (+6dB) !!!", 5000)
            viewModelScope.launch {
                delay(6000)
                _uiState.update { it.copy(isOverdrive = false) }
            }
        }
    }

    fun showTemporaryStatus(text: String, durationMs: Long = 3000) {
        tempStatusJob?.cancel()
        tempStatusJob = viewModelScope.launch {
            _uiState.update { it.copy(temporaryStatus = text) }
            delay(durationMs)
            _uiState.update { it.copy(temporaryStatus = null) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        tempStatusJob?.cancel()
        controllerFuture?.let { MediaController.releaseFuture(it) }
    }
}
