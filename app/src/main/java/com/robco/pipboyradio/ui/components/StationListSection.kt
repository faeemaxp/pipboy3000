package com.robco.pipboyradio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
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
    modifier: Modifier = Modifier
) {
    val categories = listOf("ALL", "FAVORITES", "WASTELAND", "VIVIDH BHARATI", "FM", "AKASHVANI")
    val borderColor = if (isOverdrive) AccentRed else SurfaceBorder

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("station_list_section"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = {
                Text(
                    "SCAN FREQUENCY / STATION NAME...",
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
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = PhosphorSecondary
                        )
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

        // Categories Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
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

        // Header info: Signal tally
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

            if (selectedCategory != "ALL") {
                Text(
                    text = "[ FILTER: $selectedCategory ]",
                    style = PipBoyTypography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = PhosphorSecondary
                    )
                )
            }
        }

        // Station List LazyColumn
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .testTag("stations_lazy_column"),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (stations.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "SIGNAL LOST // NO BROADCAST DETECTED",
                                style = PipBoyTypography.titleMedium.copy(
                                    color = PhosphorSecondary.copy(alpha = 0.6f),
                                    fontSize = 13.sp
                                )
                            )
                            Text(
                                text = "ADJUST FREQUENCY SEARCH OR CATEGORY FILTER",
                                style = PipBoyTypography.bodyMedium.copy(
                                    fontSize = 11.sp,
                                    color = PhosphorSecondary.copy(alpha = 0.4f)
                                ),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            } else {
                itemsIndexed(stations, key = { _, s -> s.url }) { index, station ->
                    val isActive = station.url == currentStation?.url
                    val isStationPlaying = isActive && isPlaying

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (isActive) 1.dp else 0.5.dp,
                                color = if (isActive) {
                                    if (isOverdrive) AccentRed else PhosphorPrimary
                                } else {
                                    SurfaceBorder.copy(alpha = 0.3f)
                                },
                                shape = RoundedCornerShape(6.dp)
                            )
                            .background(
                                if (isActive) {
                                    if (isOverdrive) AccentRed.copy(alpha = 0.15f) else PhosphorSecondary.copy(alpha = 0.15f)
                                } else {
                                    SurfaceCard
                                },
                                RoundedCornerShape(6.dp)
                            )
                            .clickable { onStationSelect(station) }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                            .testTag("station_item_$index"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (isActive) {
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
                                        color = if (isActive) {
                                            if (isOverdrive) AccentRed else PhosphorPrimary
                                        } else {
                                            Color(0xFFDDFFDD)
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

                        // Bookmark / Favorite Star button
                        IconButton(
                            onClick = { onToggleFavorite(station) },
                            modifier = Modifier
                                .size(44.dp)
                                .testTag("favorite_btn_$index")
                        ) {
                            Icon(
                                imageVector = if (station.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = if (station.isFavorite) "Favorited" else "Favorite",
                                tint = if (station.isFavorite) AccentAmber else PhosphorSecondary.copy(alpha = 0.4f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
