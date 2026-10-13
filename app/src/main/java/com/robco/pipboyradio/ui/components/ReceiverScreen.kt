package com.robco.pipboyradio.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.robco.pipboyradio.data.model.Station

@Composable
fun ReceiverScreen(
    currentStation: Station?,
    isPlaying: Boolean,
    statusSignal: String,
    volume: Float,
    isOverdrive: Boolean,
    activeTip: String,
    onTogglePower: () -> Unit,
    onPrevStation: () -> Unit,
    onNextStation: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    onToggleOverdrive: () -> Unit,
    onBrowseStations: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("receiver_screen"),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Top Terminal Header with Clock and Mode Switch
        TerminalHeader(
            isOverdrive = isOverdrive,
            onToggleOverdrive = onToggleOverdrive
        )

        // Hero Audio Spectrum & Oscilloscope Display — fills remaining space
        VisualizerCanvas(
            isPlaying = isPlaying,
            isOverdrive = isOverdrive,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        // Station Broadcast Telemetry with direct browse shortcut
        StatusPanel(
            statusSignal = statusSignal,
            currentStation = currentStation,
            isPlaying = isPlaying,
            isOverdrive = isOverdrive,
            onBrowseStations = onBrowseStations
        )

        // Primary Playback Console (Tactile Power, Stepper & Volume)
        ControlsSection(
            isPlaying = isPlaying,
            volume = volume,
            isOverdrive = isOverdrive,
            onTogglePower = onTogglePower,
            onPrevStation = onPrevStation,
            onNextStation = onNextStation,
            onVolumeChange = onVolumeChange
        )

        // Vault-Tec Lore & Emergency Transmission Ticker
        TerminalFooter(
            tipText = activeTip
        )
    }
}
