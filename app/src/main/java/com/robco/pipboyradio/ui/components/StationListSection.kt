package com.robco.pipboyradio.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robco.pipboyradio.data.model.Station
import com.robco.pipboyradio.ui.theme.AccentAmber
import com.robco.pipboyradio.ui.theme.AccentRed
import com.robco.pipboyradio.ui.theme.PhosphorPrimary
import com.robco.pipboyradio.ui.theme.PhosphorSecondary
import com.robco.pipboyradio.ui.theme.PipBoyTypography
import com.robco.pipboyradio.ui.theme.SurfaceBorder
import com.robco.pipboyradio.ui.theme.SurfaceCard
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StationListSection(
    stations: List<Station>,
    currentStation: Station?,
    isPlaying: Boolean,
    searchQuery: String,
    selectedCategory: String,
    isOverdrive: Boolean,
    onSearchChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onStationSelect: (Station) -> Unit,
    onToggleFavorite: (Station) -> Unit,
    onRemoveStation: (Station) -> Unit,
    onRemoveMultipleStations: (Set<String>) -> Unit = {},
    onOpenAddStation: (initialName: String, initialUrl: String) -> Unit,
    onSearchSatellite: (query: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf("ALL", "FAVORITES", "WASTELAND", "VIVIDH BHARATI", "FM", "AKASHVANI", "JAZZ", "CLASSICAL", "AMBIENT")
    val borderColor = if (isOverdrive) AccentRed else SurfaceBorder

    var isSelectionMode by rememberSaveable { mutableStateOf(false) }
    var selectedUrls by remember { mutableStateOf(setOf<String>()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("station_list_section"),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Multi-Selection Action Header (When in Multi-Select Mode)
        AnimatedVisibility(
            visible = isSelectionMode,
            enter = fadeIn(tween(150)),
            exit = fadeOut(tween(150))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (isOverdrive) AccentRed else AccentAmber, RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E160A), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .testTag("selection_action_bar")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = {
                                isSelectionMode = false
                                selectedUrls = emptySet()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Exit selection",
                                tint = AccentAmber,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = "[ ${selectedUrls.size} SELECTED ]",
                            style = PipBoyTypography.titleMedium.copy(
                                fontSize = 12.sp,
                                color = AccentAmber
                            )
                        )
                    }

                    // Selection Actions: ALL, INVERT, DELETE
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Select All Button
                        OutlinedButton(
                            onClick = {
                                selectedUrls = stations.map { it.url }.toSet()
                            },
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PhosphorPrimary),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(PhosphorPrimary.copy(alpha = 0.5f))
                            ),
                            modifier = Modifier
                                .height(30.dp)
                                .testTag("select_all_btn")
                        ) {
                            Text("ALL", style = PipBoyTypography.labelSmall.copy(fontSize = 10.sp, color = PhosphorPrimary))
                        }

                        // Invert Selection Button
                        OutlinedButton(
                            onClick = {
                                val allUrls = stations.map { it.url }.toSet()
                                selectedUrls = allUrls - selectedUrls
                            },
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PhosphorSecondary),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(PhosphorSecondary.copy(alpha = 0.5f))
                            ),
                            modifier = Modifier
                                .height(30.dp)
                                .testTag("invert_selection_btn")
                        ) {
                            Text("INVERT", style = PipBoyTypography.labelSmall.copy(fontSize = 9.sp, color = PhosphorSecondary))
                        }

                        // Delete Selected Button
                        Button(
                            onClick = {
                                if (selectedUrls.isNotEmpty()) {
                                    onRemoveMultipleStations(selectedUrls)
                                    isSelectionMode = false
                                    selectedUrls = emptySet()
                                }
                            },
                            enabled = selectedUrls.isNotEmpty(),
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentRed,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .height(30.dp)
                                .testTag("delete_selected_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color.Black,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "DELETE (${selectedUrls.size})",
                                style = PipBoyTypography.labelSmall.copy(fontSize = 10.sp, color = Color.Black),
                                modifier = Modifier.padding(start = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Standard Search & Filter Header (Hidden while multi-selecting to avoid clutter)
        if (!isSelectionMode) {
            // Search bar with integrated "+" Add button and Clear button
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = {
                    Text(
                        "SCAN / TYPE NAME OR URL TO ADD...",
                        style = PipBoyTypography.bodyMedium.copy(fontSize = 12.sp)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = if (isOverdrive) AccentRed else PhosphorPrimary
                    )
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Quick '+' add button in search bar when query is typed
                        if (searchQuery.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    val isUrl = searchQuery.startsWith("http://") || searchQuery.startsWith("https://")
                                    onOpenAddStation(
                                        if (isUrl) "" else searchQuery,
                                        if (isUrl) searchQuery else ""
                                    )
                                },
                                modifier = Modifier.testTag("search_bar_quick_add_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add as station",
                                    tint = AccentAmber,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear search",
                                    tint = PhosphorSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isOverdrive) AccentRed else PhosphorPrimary,
                    unfocusedBorderColor = SurfaceBorder.copy(alpha = 0.5f),
                    focusedTextColor = if (isOverdrive) AccentRed else PhosphorPrimary,
                    unfocusedTextColor = PhosphorSecondary,
                    cursorColor = AccentAmber,
                    focusedContainerColor = SurfaceCard,
                    unfocusedContainerColor = SurfaceCard
                ),
                textStyle = PipBoyTypography.bodyLarge.copy(fontSize = 13.sp),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_input")
            )

            // Categories Row & Direct "+ ADD STATION" Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Horizontal Categories Scroll
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        val label = if (cat == "FAVORITES") "★ FAVORITES" else cat
                        Box(
                            modifier = Modifier
                                .border(
                                    1.dp,
                                    if (isSelected) (if (isOverdrive) AccentRed else PhosphorPrimary) else SurfaceBorder.copy(alpha = 0.4f),
                                    RoundedCornerShape(6.dp)
                                )
                                .background(
                                    if (isSelected) {
                                        if (isOverdrive) AccentRed.copy(alpha = 0.25f) else PhosphorSecondary.copy(alpha = 0.3f)
                                    } else {
                                        Color.Transparent
                                    },
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { onCategoryChange(cat) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("category_chip_$cat")
                        ) {
                            Text(
                                text = label,
                                style = PipBoyTypography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    color = if (isSelected) {
                                        if (isOverdrive) AccentRed else PhosphorPrimary
                                    } else {
                                        PhosphorSecondary
                                    }
                                )
                            )
                        }
                    }
                }

                // Quick "+ ADD" Button in bar
                Button(
                    onClick = {
                        val isUrl = searchQuery.startsWith("http://") || searchQuery.startsWith("https://")
                        onOpenAddStation(
                            if (isUrl) "" else searchQuery,
                            if (isUrl) searchQuery else ""
                        )
                    },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isOverdrive) AccentRed else PhosphorPrimary,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .height(32.dp)
                        .testTag("tuner_header_add_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "ADD",
                        style = PipBoyTypography.labelSmall.copy(fontSize = 11.sp, color = Color.Black),
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }

            // Sub-header row: Frequency tally & Enter Multi-Select shortcut
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "> ${stations.size} FREQUENCIES DETECTED",
                    style = PipBoyTypography.labelSmall.copy(
                        fontSize = 11.sp,
                        color = AccentAmber
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Quick Multi-Select Mode Trigger Button
                    Box(
                        modifier = Modifier
                            .border(1.dp, SurfaceBorder.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                            .background(Color(0x22000000), RoundedCornerShape(4.dp))
                            .clickable {
                                isSelectionMode = true
                                selectedUrls = emptySet()
                            }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .testTag("enter_select_mode_btn")
                    ) {
                        Text(
                            text = "SELECT",
                            style = PipBoyTypography.labelSmall.copy(fontSize = 9.sp, color = PhosphorSecondary)
                        )
                    }

                    if (selectedCategory != "ALL") {
                        Text(
                            text = "[ $selectedCategory ]",
                            style = PipBoyTypography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = PhosphorSecondary
                            )
                        )
                    }
                }
            }
        }

        // Action banner when search query is typed: Quick Add or Satellite Search
        if (searchQuery.isNotBlank() && !isSelectionMode) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceBorder.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                    .background(SurfaceCard, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SEARCH: \"$searchQuery\"",
                        style = PipBoyTypography.labelSmall.copy(fontSize = 10.sp, color = PhosphorSecondary),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(
                            onClick = {
                                val isUrl = searchQuery.startsWith("http://") || searchQuery.startsWith("https://")
                                onOpenAddStation(
                                    if (isUrl) "" else searchQuery,
                                    if (isUrl) searchQuery else ""
                                )
                            },
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PhosphorPrimary),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(PhosphorPrimary.copy(alpha = 0.6f))
                            ),
                            modifier = Modifier
                                .height(28.dp)
                                .testTag("banner_add_station_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = PhosphorPrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "ADD NEW",
                                style = PipBoyTypography.labelSmall.copy(fontSize = 9.sp, color = PhosphorPrimary),
                                modifier = Modifier.padding(start = 2.dp)
                            )
                        }

                        Button(
                            onClick = { onSearchSatellite(searchQuery) },
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentAmber,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .height(28.dp)
                                .testTag("banner_satellite_search_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "SATELLITE",
                                style = PipBoyTypography.labelSmall.copy(fontSize = 9.sp, color = Color.Black),
                                modifier = Modifier.padding(start = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Station List LazyColumn
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .testTag("stations_lazy_column"),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (stations.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "SIGNAL LOST // NO BROADCAST DETECTED",
                                style = PipBoyTypography.titleMedium.copy(
                                    color = PhosphorSecondary.copy(alpha = 0.7f),
                                    fontSize = 13.sp
                                )
                            )

                            if (searchQuery.isNotBlank()) {
                                Text(
                                    text = "NO LOCAL STATIONS MATCH \"$searchQuery\"",
                                    style = PipBoyTypography.bodyMedium.copy(
                                        fontSize = 11.sp,
                                        color = AccentAmber
                                    )
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            val isUrl = searchQuery.startsWith("http://") || searchQuery.startsWith("https://")
                                            onOpenAddStation(
                                                if (isUrl) "" else searchQuery,
                                                if (isUrl) searchQuery else ""
                                            )
                                        },
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isOverdrive) AccentRed else PhosphorPrimary,
                                            contentColor = Color.Black
                                        ),
                                        modifier = Modifier.testTag("empty_state_add_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "+ ADD AS STATION",
                                            style = PipBoyTypography.labelSmall.copy(fontSize = 10.sp, color = Color.Black),
                                            modifier = Modifier.padding(start = 4.dp)
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = { onSearchSatellite(searchQuery) },
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentAmber),
                                        border = ButtonDefaults.outlinedButtonBorder.copy(
                                            brush = androidx.compose.ui.graphics.SolidColor(AccentAmber.copy(alpha = 0.6f))
                                        ),
                                        modifier = Modifier.testTag("empty_state_satellite_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Public,
                                            contentDescription = null,
                                            tint = AccentAmber,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "SCAN SATELLITES",
                                            style = PipBoyTypography.labelSmall.copy(fontSize = 10.sp, color = AccentAmber),
                                            modifier = Modifier.padding(start = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                itemsIndexed(stations, key = { index, s -> "${s.url}_$index" }) { index, station ->
                    val isActive = station.url == currentStation?.url
                    val isStationPlaying = isActive && isPlaying
                    val isSelected = selectedUrls.contains(station.url)

                    TelegramSwipeableStationItem(
                        station = station,
                        index = index,
                        isActive = isActive,
                        isStationPlaying = isStationPlaying,
                        isOverdrive = isOverdrive,
                        isSelectionMode = isSelectionMode,
                        isSelected = isSelected,
                        onStationSelect = {
                            if (isSelectionMode) {
                                selectedUrls = if (isSelected) selectedUrls - station.url else selectedUrls + station.url
                            } else {
                                onStationSelect(station)
                            }
                        },
                        onStationLongPress = {
                            if (!isSelectionMode) {
                                isSelectionMode = true
                                selectedUrls = setOf(station.url)
                            } else {
                                selectedUrls = if (isSelected) selectedUrls - station.url else selectedUrls + station.url
                            }
                        },
                        onToggleFavorite = { onToggleFavorite(station) },
                        onRemoveStation = { onRemoveStation(station) }
                    )
                }
            }
        }
    }
}

/**
 * Telegram-Style Swipeable Row:
 * - Swiping from right to left reveals a red Telegram-like "DELETE" button.
 * - Tapping the revealed delete button deletes the station.
 * - Swiping past threshold (> 140dp) auto-deletes the station.
 * - Press & hold (long press) enters Multi-Select Mode to select more, select all, etc.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TelegramSwipeableStationItem(
    station: Station,
    index: Int,
    isActive: Boolean,
    isStationPlaying: Boolean,
    isOverdrive: Boolean,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onStationSelect: () -> Unit,
    onStationLongPress: () -> Unit,
    onToggleFavorite: () -> Unit,
    onRemoveStation: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val revealWidth = 84.dp
    val revealWidthPx = with(density) { revealWidth.toPx() }
    val fullSwipeThresholdPx = with(density) { 150.dp.toPx() }

    val offsetX = remember { Animatable(0f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF0F1511))
            .testTag("station_item_container_$index")
    ) {
        // Background revealed Telegram-style DELETE button on the right
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(AccentRed, RoundedCornerShape(6.dp))
                .clickable {
                    coroutineScope.launch {
                        offsetX.animateTo(0f, tween(100))
                        onRemoveStation()
                    }
                }
                .padding(end = 16.dp)
                .testTag("swipe_delete_action_$index"),
            contentAlignment = Alignment.CenterEnd
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "DELETE",
                    style = PipBoyTypography.labelSmall.copy(
                        fontSize = 11.sp,
                        color = Color.Black
                    )
                )
            }
        }

        // Foreground Station Card (Draggable horizontally to reveal delete button)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .pointerInput(isSelectionMode) {
                    if (!isSelectionMode) {
                        detectHorizontalDragGestures(
                            onHorizontalDrag = { _, dragAmount ->
                                val target = (offsetX.value + dragAmount).coerceIn(-fullSwipeThresholdPx * 1.3f, 0f)
                                coroutineScope.launch { offsetX.snapTo(target) }
                            },
                            onDragEnd = {
                                coroutineScope.launch {
                                    if (offsetX.value < -fullSwipeThresholdPx) {
                                        // Full swipe to dismiss and delete!
                                        offsetX.animateTo(-fullSwipeThresholdPx * 2, tween(150))
                                        onRemoveStation()
                                    } else if (offsetX.value < -revealWidthPx * 0.4f) {
                                        // Snap open to reveal Telegram delete option
                                        offsetX.animateTo(-revealWidthPx, tween(150))
                                    } else {
                                        // Snap closed
                                        offsetX.animateTo(0f, tween(150))
                                    }
                                }
                            },
                            onDragCancel = {
                                coroutineScope.launch { offsetX.animateTo(0f, tween(150)) }
                            }
                        )
                    }
                }
                .border(
                    width = if (isSelected) 1.5.dp else if (isActive) 1.dp else 0.5.dp,
                    color = when {
                        isSelected -> AccentAmber
                        isActive -> if (isOverdrive) AccentRed else PhosphorPrimary
                        else -> SurfaceBorder.copy(alpha = 0.3f)
                    },
                    shape = RoundedCornerShape(6.dp)
                )
                .background(
                    when {
                        isSelected -> AccentAmber.copy(alpha = 0.2f)
                        isActive -> if (isOverdrive) AccentRed.copy(alpha = 0.15f) else PhosphorSecondary.copy(alpha = 0.15f)
                        else -> SurfaceCard
                    },
                    RoundedCornerShape(6.dp)
                )
                .combinedClickable(
                    onClick = {
                        if (offsetX.value < -5f) {
                            coroutineScope.launch { offsetX.animateTo(0f, tween(150)) }
                        } else {
                            onStationSelect()
                        }
                    },
                    onLongClick = onStationLongPress
                )
                .padding(horizontal = 10.dp, vertical = 7.dp)
                .testTag("station_item_$index"),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Multi-select Checkbox indicator
                if (isSelectionMode) {
                    Icon(
                        imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = if (isSelected) "Selected" else "Not Selected",
                        tint = if (isSelected) AccentAmber else PhosphorSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Station Info Column
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (isActive && !isSelectionMode) {
                            Text(
                                text = if (isStationPlaying) "►" else "■",
                                style = PipBoyTypography.bodyLarge.copy(
                                    color = if (isOverdrive) AccentRed else AccentAmber,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        Text(
                            text = station.name,
                            style = PipBoyTypography.bodyLarge.copy(
                                color = when {
                                    isSelected -> AccentAmber
                                    isActive -> if (isOverdrive) AccentRed else PhosphorPrimary
                                    else -> Color(0xFFDDFFDD)
                                },
                                fontSize = 13.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        text = "[ ${station.category.uppercase()} ]",
                        style = PipBoyTypography.bodyMedium.copy(
                            fontSize = 10.sp,
                            color = PhosphorSecondary.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // Right Action Buttons (Favorite Star & Quick Delete)
            if (!isSelectionMode) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("favorite_btn_$index")
                    ) {
                        Icon(
                            imageVector = if (station.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = if (station.isFavorite) "Favorited" else "Favorite",
                            tint = if (station.isFavorite) AccentAmber else PhosphorSecondary.copy(alpha = 0.4f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Delete button (can also be swiped to reveal!)
                    IconButton(
                        onClick = onRemoveStation,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("delete_station_btn_$index")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete station",
                            tint = AccentRed.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
