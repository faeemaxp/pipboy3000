package com.robco.pipboyradio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.robco.pipboyradio.data.model.Station
import com.robco.pipboyradio.data.model.StreamMetadata
import com.robco.pipboyradio.ui.theme.AccentAmber
import com.robco.pipboyradio.ui.theme.AccentRed
import com.robco.pipboyradio.ui.theme.PhosphorPrimary
import com.robco.pipboyradio.ui.theme.PhosphorSecondary
import com.robco.pipboyradio.ui.theme.PipBoyTypography
import com.robco.pipboyradio.ui.theme.ScreenBackground
import com.robco.pipboyradio.ui.theme.SurfaceBorder
import com.robco.pipboyradio.ui.theme.SurfaceCard

@Composable
fun MetadataDialog(
    isOpen: Boolean,
    station: Station?,
    metadata: StreamMetadata,
    isPlaying: Boolean,
    isOverdrive: Boolean,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

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
                .testTag("metadata_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = if (isOverdrive) AccentRed else PhosphorPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "[ SIGNAL METADATA ]",
                            style = PipBoyTypography.titleMedium.copy(
                                color = if (isOverdrive) AccentRed else PhosphorPrimary,
                                fontSize = 15.sp
                            )
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = PhosphorSecondary
                        )
                    }
                }

                // Now Playing Hero Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, borderColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .background(SurfaceCard, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = AccentAmber,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (isPlaying) "NOW BROADCASTING" else "LAST TUNED FREQUENCY",
                                style = PipBoyTypography.labelSmall.copy(fontSize = 10.sp, color = AccentAmber)
                            )
                        }

                        Text(
                            text = metadata.title?.takeIf { it.isNotBlank() } ?: (station?.name ?: "UNKNOWN BROADCAST"),
                            style = PipBoyTypography.titleMedium.copy(
                                fontSize = 14.sp,
                                color = if (isOverdrive) AccentRed else PhosphorPrimary
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (!metadata.artist.isNullOrBlank()) {
                            Text(
                                text = "ARTIST: ${metadata.artist}",
                                style = PipBoyTypography.bodyMedium.copy(fontSize = 12.sp, color = Color(0xFFDDFFDD))
                            )
                        }
                    }
                }

                // Station Specs Table
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SurfaceBorder.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                        .background(SurfaceCard, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "> TRANSMISSION SPECS:",
                            style = PipBoyTypography.labelSmall.copy(fontSize = 10.sp, color = PhosphorSecondary)
                        )

                        MetadataItem(label = "STATION CALLSIGN", value = station?.name ?: "---")
                        MetadataItem(label = "FREQUENCY BAND", value = metadata.frequencyDial)
                        MetadataItem(label = "GENRE / CATEGORY", value = station?.category?.uppercase() ?: "ALL")
                        MetadataItem(label = "STREAM FORMAT", value = metadata.streamType)
                        MetadataItem(label = "BITRATE", value = metadata.bitrate)
                        MetadataItem(label = "AUDIO CHANNELS", value = metadata.channels)
                        MetadataItem(label = "SAMPLE RATE", value = metadata.sampleRate)
                        MetadataItem(label = "SIGNAL QUALITY", value = if (isPlaying) metadata.signalQuality else "STANDBY")
                    }
                }

                // Direct Stream Source Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SurfaceBorder.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                        .background(Color(0x33000000), RoundedCornerShape(6.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "> STREAM SOURCE URL:",
                            style = PipBoyTypography.labelSmall.copy(fontSize = 9.sp, color = PhosphorSecondary)
                        )
                        Text(
                            text = station?.url ?: "---",
                            style = PipBoyTypography.bodyMedium.copy(fontSize = 10.sp, color = Color(0xFFB0FFB0)),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Close Button
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isOverdrive) AccentRed else PhosphorPrimary,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    Text(
                        text = "[ CLOSE METADATA ]",
                        style = PipBoyTypography.labelSmall.copy(fontSize = 11.sp, color = Color.Black)
                    )
                }
            }
        }
    }
}

@Composable
private fun MetadataItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = PipBoyTypography.bodyMedium.copy(fontSize = 11.sp, color = PhosphorSecondary)
        )
        Text(
            text = value,
            style = PipBoyTypography.bodyLarge.copy(fontSize = 11.sp, color = PhosphorPrimary)
        )
    }
}
