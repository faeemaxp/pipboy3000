package com.robco.pipboyradio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.robco.pipboyradio.data.model.Station
import com.robco.pipboyradio.ui.theme.AccentAmber
import com.robco.pipboyradio.ui.theme.AccentRed
import com.robco.pipboyradio.ui.theme.PhosphorPrimary
import com.robco.pipboyradio.ui.theme.PhosphorSecondary
import com.robco.pipboyradio.ui.theme.PipBoyTypography
import com.robco.pipboyradio.ui.theme.ScreenBackground
import com.robco.pipboyradio.ui.theme.SurfaceBorder
import com.robco.pipboyradio.ui.theme.SurfaceCard

@Composable
fun AddStationDialog(
    isOpen: Boolean,
    initialName: String = "",
    initialUrl: String = "",
    isOverdrive: Boolean,
    onDismiss: () -> Unit,
    onAddStation: (Station) -> Unit
) {
    if (!isOpen) return

    var stationName by rememberSaveable { mutableStateOf(initialName) }
    var streamUrl by rememberSaveable { mutableStateOf(initialUrl) }
    var selectedCategory by rememberSaveable { mutableStateOf("WASTELAND") }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(isOpen, initialName, initialUrl) {
        if (isOpen) {
            if (initialName.isNotBlank() && stationName.isBlank()) {
                stationName = initialName
            }
            if (initialUrl.isNotBlank() && streamUrl.isBlank()) {
                streamUrl = initialUrl
            }
        }
    }

    val categories = listOf("WASTELAND", "FM", "JAZZ", "CLASSICAL", "AMBIENT", "POP", "AKASHVANI", "CUSTOM")
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
                .testTag("add_station_dialog")
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
                            imageVector = Icons.Default.Radio,
                            contentDescription = null,
                            tint = if (isOverdrive) AccentRed else PhosphorPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "[ ADD NEW STATION ]",
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

                Text(
                    text = "> TRANSMIT CUSTOM FREQUENCY TO TUNER MEMORY",
                    style = PipBoyTypography.labelSmall.copy(
                        color = AccentAmber,
                        fontSize = 10.sp
                    )
                )

                // Station Name Input
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "STATION CALLSIGN / NAME:",
                        style = PipBoyTypography.labelSmall.copy(fontSize = 10.sp, color = PhosphorSecondary)
                    )
                    OutlinedTextField(
                        value = stationName,
                        onValueChange = {
                            stationName = it
                            errorMessage = null
                        },
                        placeholder = {
                            Text(
                                "e.g. New Vegas Radio, Jazz 24...",
                                style = PipBoyTypography.bodyMedium.copy(fontSize = 11.sp, color = PhosphorSecondary.copy(alpha = 0.5f))
                            )
                        },
                        singleLine = true,
                        textStyle = PipBoyTypography.bodyLarge.copy(fontSize = 13.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isOverdrive) AccentRed else PhosphorPrimary,
                            unfocusedBorderColor = SurfaceBorder.copy(alpha = 0.4f),
                            focusedTextColor = PhosphorPrimary,
                            unfocusedTextColor = Color(0xFFDDFFDD),
                            cursorColor = AccentAmber,
                            focusedContainerColor = SurfaceCard,
                            unfocusedContainerColor = SurfaceCard
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_station_name_input")
                    )
                }

                // Stream URL Input
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "STREAM AUDIO URL (MP3 / AAC / HLS M3U8):",
                        style = PipBoyTypography.labelSmall.copy(fontSize = 10.sp, color = PhosphorSecondary)
                    )
                    OutlinedTextField(
                        value = streamUrl,
                        onValueChange = {
                            streamUrl = it
                            errorMessage = null
                        },
                        placeholder = {
                            Text(
                                "https://live.example.com/stream.mp3",
                                style = PipBoyTypography.bodyMedium.copy(fontSize = 11.sp, color = PhosphorSecondary.copy(alpha = 0.5f))
                            )
                        },
                        singleLine = true,
                        textStyle = PipBoyTypography.bodyMedium.copy(fontSize = 12.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isOverdrive) AccentRed else PhosphorPrimary,
                            unfocusedBorderColor = SurfaceBorder.copy(alpha = 0.4f),
                            focusedTextColor = PhosphorPrimary,
                            unfocusedTextColor = Color(0xFFDDFFDD),
                            cursorColor = AccentAmber,
                            focusedContainerColor = SurfaceCard,
                            unfocusedContainerColor = SurfaceCard
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_station_url_input")
                    )
                }

                // Category selector pills
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "SIGNAL CATEGORY / BAND:",
                        style = PipBoyTypography.labelSmall.copy(fontSize = 10.sp, color = PhosphorSecondary)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSel = selectedCategory == cat
                            Box(
                                modifier = Modifier
                                    .border(
                                        1.dp,
                                        if (isSel) (if (isOverdrive) AccentRed else PhosphorPrimary) else SurfaceBorder.copy(alpha = 0.4f),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .background(
                                        if (isSel) PhosphorSecondary.copy(alpha = 0.3f) else Color.Transparent,
                                        RoundedCornerShape(4.dp)
                                    )
                                    .clickable { selectedCategory = cat }
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                                    .testTag("add_category_chip_$cat")
                            ) {
                                Text(
                                    text = cat,
                                    style = PipBoyTypography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        color = if (isSel) PhosphorPrimary else PhosphorSecondary
                                    )
                                )
                            }
                        }
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = "> ERR: $errorMessage",
                        style = PipBoyTypography.labelSmall.copy(fontSize = 10.sp, color = AccentRed),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PhosphorSecondary),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder.copy(alpha = 0.5f))
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                    ) {
                        Text(
                            text = "CANCEL",
                            style = PipBoyTypography.labelSmall.copy(fontSize = 11.sp, color = PhosphorSecondary)
                        )
                    }

                    Button(
                        onClick = {
                            val trimmedName = stationName.trim()
                            var trimmedUrl = streamUrl.trim()

                            if (trimmedName.isBlank()) {
                                errorMessage = "STATION NAME CANNOT BE EMPTY"
                                return@Button
                            }
                            if (trimmedUrl.isBlank()) {
                                errorMessage = "STREAM URL CANNOT BE EMPTY"
                                return@Button
                            }

                            if (!trimmedUrl.startsWith("http://") && !trimmedUrl.startsWith("https://")) {
                                trimmedUrl = "https://$trimmedUrl"
                            }

                            val newStation = Station(
                                id = "custom-${System.currentTimeMillis()}",
                                name = trimmedName,
                                url = trimmedUrl,
                                category = selectedCategory
                            )
                            onAddStation(newStation)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isOverdrive) AccentRed else PhosphorPrimary,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(42.dp)
                            .testTag("submit_add_station_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "LOCK & ADD",
                            style = PipBoyTypography.labelSmall.copy(fontSize = 11.sp, color = Color.Black),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
