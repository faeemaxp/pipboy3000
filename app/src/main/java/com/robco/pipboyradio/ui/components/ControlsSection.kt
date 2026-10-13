package com.robco.pipboyradio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robco.pipboyradio.ui.theme.AccentAmber
import com.robco.pipboyradio.ui.theme.AccentRed
import com.robco.pipboyradio.ui.theme.PhosphorDim
import com.robco.pipboyradio.ui.theme.PhosphorPrimary
import com.robco.pipboyradio.ui.theme.PhosphorSecondary
import com.robco.pipboyradio.ui.theme.PipBoyTypography
import com.robco.pipboyradio.ui.theme.SurfaceBorder
import com.robco.pipboyradio.ui.theme.SurfaceCard
import kotlin.math.roundToInt

@Composable
fun ControlsSection(
    isPlaying: Boolean,
    volume: Float,
    isOverdrive: Boolean,
    onTogglePower: () -> Unit,
    onPrevStation: () -> Unit,
    onNextStation: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isOverdrive) AccentRed else SurfaceBorder

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .background(SurfaceCard, RoundedCornerShape(10.dp))
            .padding(10.dp)
            .testTag("controls_section"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "[ PLAYBACK CONSOLE ]",
                style = PipBoyTypography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = if (isOverdrive) AccentRed else PhosphorPrimary
                )
            )

            Text(
                text = if (isPlaying) "VACUUM TUBES: ACTIVE" else "VACUUM TUBES: WARM",
                style = PipBoyTypography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = PhosphorSecondary
                )
            )
        }

        // Primary Large Power Button (52dp height, bold tactile button)
        Button(
            onClick = onTogglePower,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isPlaying) {
                    if (isOverdrive) AccentRed else Color(0xFFD32F2F)
                } else {
                    PhosphorPrimary
                },
                contentColor = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .testTag("power_btn")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(22.dp)
                )

                Text(
                    text = if (isPlaying) "TERMINATE BROADCAST" else "INITIALIZE BROADCAST",
                    style = PipBoyTypography.titleMedium.copy(
                        color = Color.Black,
                        fontSize = 14.sp,
                        letterSpacing = 1.sp
                    )
                )
            }
        }

        // Stepper Navigation Row (◄ PREV / NEXT ►)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onPrevStation,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PhosphorPrimary
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder.copy(alpha = 0.6f))
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .testTag("prev_station_btn")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = null,
                        tint = PhosphorPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "PREV",
                        style = PipBoyTypography.bodyMedium.copy(
                            color = PhosphorPrimary,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            OutlinedButton(
                onClick = onNextStation,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PhosphorPrimary
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder.copy(alpha = 0.6f))
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .testTag("next_station_btn")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "NEXT",
                        style = PipBoyTypography.bodyMedium.copy(
                            color = PhosphorPrimary,
                            fontSize = 12.sp
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = null,
                        tint = PhosphorPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Volume Console Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(androidx.compose.ui.graphics.Color(0x33000000), RoundedCornerShape(6.dp))
                .border(1.dp, SurfaceBorder.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = "Volume",
                tint = if (isOverdrive) AccentRed else PhosphorPrimary,
                modifier = Modifier.size(18.dp)
            )

            Text(
                text = "VOL",
                style = PipBoyTypography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = PhosphorSecondary
                )
            )

            Slider(
                value = volume,
                onValueChange = onVolumeChange,
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    thumbColor = if (isOverdrive) AccentRed else PhosphorPrimary,
                    activeTrackColor = if (isOverdrive) AccentRed else PhosphorPrimary,
                    inactiveTrackColor = PhosphorDim
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("volume_slider")
            )

            Text(
                text = "${(volume * 100).roundToInt()}%",
                style = PipBoyTypography.labelSmall.copy(
                    color = AccentAmber,
                    fontSize = 12.sp
                ),
                modifier = Modifier.width(44.dp)
            )
        }
    }
}
