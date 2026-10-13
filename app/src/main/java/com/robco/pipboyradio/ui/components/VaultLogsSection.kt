package com.robco.pipboyradio.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robco.pipboyradio.ui.theme.AccentAmber
import com.robco.pipboyradio.ui.theme.AccentRed
import com.robco.pipboyradio.ui.theme.PhosphorPrimary
import com.robco.pipboyradio.ui.theme.PhosphorSecondary
import com.robco.pipboyradio.ui.theme.PipBoyTypography
import com.robco.pipboyradio.ui.theme.SurfaceBorder
import com.robco.pipboyradio.ui.theme.SurfaceCard

@Composable
fun VaultLogsSection(
    isOverdrive: Boolean,
    isRepoLoading: Boolean = false,
    repoStatusMessage: String? = null,
    totalStationCount: Int = 0,
    onLoadFromRawJsonUrl: (String) -> Unit = {},
    onLoadFromRadioBrowser: (filterType: String, filterValue: String) -> Unit = { _, _ -> },
    onLoadBundledStations: () -> Unit = {},
    onClearStations: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val borderColor = if (isOverdrive) AccentRed else SurfaceBorder

    var githubUrlInput by rememberSaveable {
        mutableStateOf("https://raw.githubusercontent.com/faeemaxp/pipboy3000/main/app/src/main/assets/stations.json")
    }
    var radioBrowserQuery by rememberSaveable { mutableStateOf("pop") }
    var selectedRadioBrowserFilter by rememberSaveable { mutableStateOf("tag") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 4.dp)
            .testTag("vault_logs_section"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Station Repository Manager Card (Add GitHub raw JSON or Radio-Browser)
        LogCard(
            title = "STATION REPOSITORY & RADIO-BROWSER API",
            borderColor = borderColor
        ) {
            Text(
                text = "> REPOSITORY STATUS: ${if (totalStationCount == 0) "NO STATIONS LOADED (DEFAULT)" else "$totalStationCount FREQUENCIES ACTIVE"}",
                style = PipBoyTypography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = if (totalStationCount == 0) AccentAmber else PhosphorPrimary
                )
            )

            if (repoStatusMessage != null) {
                Text(
                    text = "> $repoStatusMessage",
                    style = PipBoyTypography.bodyMedium.copy(
                        fontSize = 10.sp,
                        color = if (repoStatusMessage.startsWith("ERR")) AccentRed else PhosphorSecondary
                    ),
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }

            if (isRepoLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = if (isOverdrive) AccentRed else PhosphorPrimary
                    )
                    Text(
                        text = "COMMUNICATING WITH SATELLITE RELAY...",
                        style = PipBoyTypography.labelSmall.copy(fontSize = 10.sp, color = AccentAmber)
                    )
                }
            }

            // Input: GitHub Raw JSON URL
            Text(
                text = "> GITHUB RAW JSON REPOSITORY URL:",
                style = PipBoyTypography.labelSmall.copy(fontSize = 10.sp, color = PhosphorSecondary),
                modifier = Modifier.padding(top = 4.dp)
            )

            OutlinedTextField(
                value = githubUrlInput,
                onValueChange = { githubUrlInput = it },
                singleLine = true,
                textStyle = PipBoyTypography.bodyMedium.copy(fontSize = 11.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isOverdrive) AccentRed else PhosphorPrimary,
                    unfocusedBorderColor = SurfaceBorder.copy(alpha = 0.5f),
                    focusedTextColor = if (isOverdrive) AccentRed else PhosphorPrimary,
                    unfocusedTextColor = PhosphorSecondary,
                    cursorColor = AccentAmber,
                    focusedContainerColor = Color(0x33000000),
                    unfocusedContainerColor = Color(0x33000000)
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("github_raw_url_input")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onLoadFromRawJsonUrl(githubUrlInput) },
                    enabled = !isRepoLoading && githubUrlInput.isNotBlank(),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isOverdrive) AccentRed else PhosphorPrimary,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDownload,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "SYNC GITHUB JSON",
                        style = PipBoyTypography.labelSmall.copy(fontSize = 11.sp, color = Color.Black),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                OutlinedButton(
                    onClick = { onClearStations() },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentRed),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(AccentRed.copy(alpha = 0.6f))
                    ),
                    modifier = Modifier.height(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear",
                        tint = AccentRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "CLEAR",
                        style = PipBoyTypography.labelSmall.copy(fontSize = 10.sp, color = AccentRed),
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }

            // Radio-Browser API Live Search
            Text(
                text = "> RADIO-BROWSER API LIVE QUERY:",
                style = PipBoyTypography.labelSmall.copy(fontSize = 10.sp, color = PhosphorSecondary),
                modifier = Modifier.padding(top = 8.dp)
            )

            // Filter Selector Pills (Tag / Country / Language)
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("tag" to "TAG", "country" to "COUNTRY", "language" to "LANGUAGE").forEach { (type, label) ->
                    val isSel = selectedRadioBrowserFilter == type
                    Box(
                        modifier = Modifier
                            .border(
                                1.dp,
                                if (isSel) (if (isOverdrive) AccentRed else PhosphorPrimary) else SurfaceBorder.copy(alpha = 0.4f),
                                RoundedCornerShape(4.dp)
                            )
                            .background(
                                if (isSel) PhosphorSecondary.copy(alpha = 0.25f) else Color.Transparent,
                                RoundedCornerShape(4.dp)
                            )
                            .clickable {
                                selectedRadioBrowserFilter = type
                                if (type == "country" && radioBrowserQuery == "pop") radioBrowserQuery = "United States"
                                if (type == "language" && radioBrowserQuery == "pop") radioBrowserQuery = "English"
                                if (type == "tag" && (radioBrowserQuery == "United States" || radioBrowserQuery == "English")) radioBrowserQuery = "pop"
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = label,
                            style = PipBoyTypography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = if (isSel) PhosphorPrimary else PhosphorSecondary
                            )
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = radioBrowserQuery,
                    onValueChange = { radioBrowserQuery = it },
                    singleLine = true,
                    placeholder = { Text("Query ($selectedRadioBrowserFilter)...", style = PipBoyTypography.bodyMedium.copy(fontSize = 11.sp)) },
                    textStyle = PipBoyTypography.bodyMedium.copy(fontSize = 11.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isOverdrive) AccentRed else PhosphorPrimary,
                        unfocusedBorderColor = SurfaceBorder.copy(alpha = 0.5f),
                        focusedTextColor = if (isOverdrive) AccentRed else PhosphorPrimary,
                        unfocusedTextColor = PhosphorSecondary,
                        cursorColor = AccentAmber,
                        focusedContainerColor = Color(0x33000000),
                        unfocusedContainerColor = Color(0x33000000)
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = { onLoadFromRadioBrowser(selectedRadioBrowserFilter, radioBrowserQuery) },
                    enabled = !isRepoLoading && radioBrowserQuery.isNotBlank(),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentAmber,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.height(52.dp)
                ) {
                    Text(
                        text = "QUERY API",
                        style = PipBoyTypography.labelSmall.copy(fontSize = 10.sp, color = Color.Black)
                    )
                }
            }

            // Quick bundled fallback button
            OutlinedButton(
                onClick = { onLoadBundledStations() },
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
            ) {
                Text(
                    text = "[ LOAD PRE-CONFIGURED 275+ VAULT STATIONS ]",
                    style = PipBoyTypography.labelSmall.copy(fontSize = 10.sp, color = PhosphorPrimary)
                )
            }
        }

        // RobCo Diagnostics Card
        LogCard(
            title = "ROBCO DIAGNOSTICS",
            borderColor = borderColor
        ) {
            LogItem(label = "HARDWARE", value = "PIP-BOY 3000 REV 4.2")
            LogItem(label = "FIRMWARE", value = "ROBCO TERMLINK v7.11")
            LogItem(label = "VACUUM TUBES", value = "STATUS: NOMINAL (CALIBRATED)")
            LogItem(label = "SIGNAL TUNER", value = "BROAD-SPECTRUM HLS & MP3")
            LogItem(label = "RADIATION FILTER", value = "ATTENUATION: 98.4%")
        }

        // Vault-Tec Emergency Frequencies
        LogCard(
            title = "EMERGENCY FREQUENCIES",
            borderColor = borderColor
        ) {
            LogItem(label = "VIVIDH BHARATI", value = "NATIONAL BROADCAST NETWORK")
            LogItem(label = "FM RAINBOW", value = "METRO RELAY TRANSMITTERS")
            LogItem(label = "AKASHVANI", value = "STATE DISPATCH STATIONS")
            LogItem(label = "DEF CON RADIO", value = "AUTOMATED CRISIS BEACON")
            LogItem(label = "SOMA DEEP SPACE", value = "INTERSTELLAR AMBIENT LINK")
        }

        // Vault Dweller Compliance Lore
        LogCard(
            title = "VAULT-TEC COMPLIANCE PROTOCOL",
            borderColor = borderColor
        ) {
            Text(
                text = "> ATTENTION CITIZEN: REGULAR LISTENING TO SANCTIONED PRE-WAR BROADCASTS IS MANDATORY FOR MENTAL EQUILIBRIUM.",
                style = PipBoyTypography.bodyMedium.copy(
                    fontSize = 11.sp,
                    color = AccentAmber
                ),
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Text(
                text = "> IN EVENT OF COMM INTERFERENCE, DO NOT PANIC. ROTATE VACUUM TUBE TUNER AND REPORT ANOMALOUS RELAYS TO THE OVERSEER.",
                style = PipBoyTypography.bodyMedium.copy(
                    fontSize = 11.sp,
                    color = PhosphorSecondary
                )
            )
        }
    }
}

@Composable
private fun LogCard(
    title: String,
    borderColor: androidx.compose.ui.graphics.Color,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .background(SurfaceCard, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "[ $title ]",
            style = PipBoyTypography.labelSmall.copy(
                fontSize = 11.sp,
                color = PhosphorPrimary
            )
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun LogItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "> $label:",
            style = PipBoyTypography.bodyMedium.copy(
                fontSize = 11.sp,
                color = PhosphorSecondary
            )
        )
        Text(
            text = value,
            style = PipBoyTypography.bodyLarge.copy(
                fontSize = 11.sp,
                color = PhosphorPrimary
            )
        )
    }
}
