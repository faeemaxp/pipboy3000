package com.robco.pipboyradio.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.robco.pipboyradio.data.model.PipBoyTab
import com.robco.pipboyradio.ui.components.MiniPlayerBar
import com.robco.pipboyradio.ui.components.PipBoyBottomNav
import com.robco.pipboyradio.ui.components.ReceiverScreen
import com.robco.pipboyradio.ui.components.StationListSection
import com.robco.pipboyradio.ui.components.VaultLogsSection
import com.robco.pipboyradio.ui.theme.ScreenBackground
import com.robco.pipboyradio.ui.viewmodel.RadioViewModel
import kotlin.math.roundToInt

@Composable
fun PipBoyRadioScreen(
    viewModel: RadioViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val filteredStations = viewModel.getFilteredStations()

    // Handle back button on sub-screens to return to Receiver
    if (uiState.currentTab != PipBoyTab.RECEIVER) {
        BackHandler {
            viewModel.setTab(PipBoyTab.RECEIVER)
        }
    }

    // Shake animation on overdrive
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(uiState.isOverdrive) {
        if (uiState.isOverdrive) {
            repeat(10) {
                shakeOffset.animateTo(4f, tween(50))
                shakeOffset.animateTo(-4f, tween(50))
            }
            shakeOffset.animateTo(0f, tween(50))
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("pipboy_scaffold"),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = ScreenBackground,
        bottomBar = {
            PipBoyBottomNav(
                currentTab = uiState.currentTab,
                isOverdrive = uiState.isOverdrive,
                onTabSelected = { viewModel.setTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Main Tab Content
                Box(modifier = Modifier.weight(1f)) {
                    AnimatedContent(
                        targetState = uiState.currentTab,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(150)) togetherWith fadeOut(animationSpec = tween(150))
                        },
                        label = "TabContentTransition"
                    ) { tab ->
                        when (tab) {
                            PipBoyTab.RECEIVER -> {
                                ReceiverScreen(
                                    currentStation = uiState.currentStation,
                                    isPlaying = uiState.isPlaying,
                                    statusSignal = uiState.displayStatus,
                                    volume = uiState.volume,
                                    isOverdrive = uiState.isOverdrive,
                                    activeTip = uiState.activeTip,
                                    onTogglePower = { viewModel.togglePower() },
                                    onPrevStation = { viewModel.prevStation() },
                                    onNextStation = { viewModel.nextStation() },
                                    onVolumeChange = { viewModel.setVolume(it) },
                                    onToggleOverdrive = { viewModel.toggleOverdrive() },
                                    onBrowseStations = { viewModel.setTab(PipBoyTab.TUNER) }
                                )
                            }

                            PipBoyTab.TUNER -> {
                                StationListSection(
                                    stations = filteredStations,
                                    currentStation = uiState.currentStation,
                                    isPlaying = uiState.isPlaying,
                                    searchQuery = uiState.searchQuery,
                                    selectedCategory = uiState.selectedCategory,
                                    isOverdrive = uiState.isOverdrive,
                                    onSearchChange = { viewModel.setSearchQuery(it) },
                                    onCategoryChange = { viewModel.setCategory(it) },
                                    onStationSelect = { viewModel.playStation(it) },
                                    onToggleFavorite = { viewModel.toggleFavorite(it) }
                                )
                            }

                            PipBoyTab.LOGS -> {
                                VaultLogsSection(
                                    isOverdrive = uiState.isOverdrive
                                )
                            }
                        }
                    }
                }

                // Mini Player docked above bottom nav when browsing TUNER or LOGS
                if (uiState.currentTab != PipBoyTab.RECEIVER) {
                    MiniPlayerBar(
                        station = uiState.currentStation,
                        isPlaying = uiState.isPlaying,
                        statusSignal = uiState.displayStatus,
                        isOverdrive = uiState.isOverdrive,
                        onTogglePower = { viewModel.togglePower() },
                        onNextStation = { viewModel.nextStation() },
                        onExpand = { viewModel.setTab(PipBoyTab.RECEIVER) }
                    )
                }
            }
        }
    }
}
