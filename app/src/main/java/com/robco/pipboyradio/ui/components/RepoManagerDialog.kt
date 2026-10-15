package com.robco.pipboyradio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderZip
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.robco.pipboyradio.ui.theme.AccentAmber
import com.robco.pipboyradio.ui.theme.AccentRed
import com.robco.pipboyradio.ui.theme.PhosphorPrimary
import com.robco.pipboyradio.ui.theme.PhosphorSecondary
import com.robco.pipboyradio.ui.theme.PipBoyTypography
import com.robco.pipboyradio.ui.theme.ScreenBackground
import com.robco.pipboyradio.ui.theme.SurfaceBorder
import com.robco.pipboyradio.ui.theme.SurfaceCard

@Composable
fun RepoManagerDialog(
    isOpen: Boolean,
    isOverdrive: Boolean,
    totalStationCount: Int,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onLoadRawJsonUrl: (url: String, append: Boolean) -> Unit,
    onLoadBundledStations: (append: Boolean) -> Unit,
    onClearAllStations: () -> Unit
) {
    if (!isOpen) return

    var customUrl by rememberSaveable {
        mutableStateOf("https://raw.githubusercontent.com/faeemaxp/pipboy3000/main/app/src/main/assets/stations.json")
    }

    val borderColor = if (isOverdrive) AccentRed else SurfaceBorder

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .background(ScreenBackground, RoundedCornerShape(12.dp))
                .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
                .padding(16.dp)
                .testTag("repo_manager_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "[ REPOSITORY MANAGER ]",
                            style = PipBoyTypography.titleMedium.copy(
                                color = if (isOverdrive) AccentRed else PhosphorPrimary,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = "ROBCO FREQUENCY REPOSITORIES // $totalStationCount ACTIVE",
                            style = PipBoyTypography.labelSmall.copy(
                                color = PhosphorSecondary,
                                fontSize = 10.sp
                            )
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = PhosphorSecondary
                        )
                    }
                }

                // Preset Repository 1: Vault-Tec Bundled Master Archive (275+ Stations)
                RepoPresetCard(
                    title = "VAULT-TEC MASTER ARCHIVE",
                    subtitle = "275+ Stations: AIR, Vividh Bharati, SomaFM & Wasteland",
                    borderColor = borderColor,
                    onReplace = { onLoadBundledStations(false) },
                    onMerge = { onLoadBundledStations(true) },
                    enabled = !isLoading
                )

                // Custom GitHub / Web Raw JSON Input Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, borderColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .background(SurfaceCard, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "> CUSTOM GITHUB RAW JSON REPOSITORY:",
                            style = PipBoyTypography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = AccentAmber
                            )
                        )

                        OutlinedTextField(
                            value = customUrl,
                            onValueChange = { customUrl = it },
                            singleLine = true,
                            textStyle = PipBoyTypography.bodyMedium.copy(fontSize = 11.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (isOverdrive) AccentRed else PhosphorPrimary,
                                unfocusedBorderColor = SurfaceBorder.copy(alpha = 0.4f),
                                focusedTextColor = PhosphorPrimary,
                                unfocusedTextColor = PhosphorSecondary,
                                cursorColor = AccentAmber,
                                focusedContainerColor = Color(0x33000000),
                                unfocusedContainerColor = Color(0x33000000)
                            ),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("modal_custom_repo_url_input")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onLoadRawJsonUrl(customUrl, false) },
                                enabled = !isLoading && customUrl.isNotBlank(),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isOverdrive) AccentRed else PhosphorPrimary,
                                    contentColor = Color.Black
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                            ) {
                                Text(
                                    text = "REPLACE ALL",
                                    style = PipBoyTypography.labelSmall.copy(fontSize = 11.sp, color = Color.Black)
                                )
                            }

                            OutlinedButton(
                                onClick = { onLoadRawJsonUrl(customUrl, true) },
                                enabled = !isLoading && customUrl.isNotBlank(),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = PhosphorPrimary
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                            ) {
                                Text(
                                    text = "+ MERGE REPO",
                                    style = PipBoyTypography.labelSmall.copy(fontSize = 11.sp, color = PhosphorPrimary)
                                )
                            }
                        }
                    }
                }

                // Purge / Clear All Stations
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AccentRed.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .background(AccentRed.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "⚠ PURGE STATION LIBRARY",
                            style = PipBoyTypography.labelSmall.copy(fontSize = 11.sp, color = AccentRed)
                        )

                        Text(
                            text = "Clears all currently loaded radio signals from the tuner.",
                            style = PipBoyTypography.bodyMedium.copy(fontSize = 10.sp, color = PhosphorSecondary)
                        )

                        Button(
                            onClick = onClearAllStations,
                            enabled = totalStationCount > 0 && !isLoading,
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentRed,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .testTag("modal_purge_all_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "PURGE ALL $totalStationCount STATIONS",
                                style = PipBoyTypography.labelSmall.copy(fontSize = 11.sp, color = Color.Black),
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }

                // Done / Dismiss Button
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PhosphorSecondary,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                ) {
                    Text(
                        text = "[ CLOSE TERMINAL ]",
                        style = PipBoyTypography.titleMedium.copy(fontSize = 12.sp, color = Color.Black)
                    )
                }
            }
        }
    }
}

@Composable
private fun RepoPresetCard(
    title: String,
    subtitle: String,
    borderColor: Color,
    onReplace: () -> Unit,
    onMerge: () -> Unit,
    enabled: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .background(SurfaceCard, RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FolderZip,
                    contentDescription = null,
                    tint = PhosphorPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = title,
                    style = PipBoyTypography.labelSmall.copy(fontSize = 11.sp, color = PhosphorPrimary)
                )
            }

            Text(
                text = subtitle,
                style = PipBoyTypography.bodyMedium.copy(fontSize = 10.sp, color = PhosphorSecondary)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onReplace,
                    enabled = enabled,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PhosphorPrimary,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                ) {
                    Text(
                        text = "REPLACE ALL",
                        style = PipBoyTypography.labelSmall.copy(fontSize = 10.sp, color = Color.Black)
                    )
                }

                OutlinedButton(
                    onClick = onMerge,
                    enabled = enabled,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PhosphorPrimary),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                ) {
                    Text(
                        text = "+ MERGE",
                        style = PipBoyTypography.labelSmall.copy(fontSize = 10.sp, color = PhosphorPrimary)
                    )
                }
            }
        }
    }
}
