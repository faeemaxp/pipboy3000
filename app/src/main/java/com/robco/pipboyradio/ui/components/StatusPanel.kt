package com.robco.pipboyradio.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
fun StatusPanel(
    statusSignal: String,
    currentStation: Station?,
    isPlaying: Boolean,
    isOverdrive: Boolean,
    onBrowseStations: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "BlinkTransition")
    val blinkAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blinkAlpha"
    )

    val borderColor = if (isOverdrive) AccentRed else SurfaceBorder
    val isAlert = statusSignal.contains("OVERDRIVE") || statusSignal.contains("ERR") || statusSignal.contains("ACQUIRING")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .background(SurfaceCard, RoundedCornerShape(10.dp))
            .padding(14.dp)
            .testTag("status_panel"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "[ BROADCAST TELEMETRY ]",
                style = PipBoyTypography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = if (isOverdrive) AccentRed else PhosphorPrimary
                )
            )

            Text(
                text = if (isPlaying) "SIGNAL: 98.4%" else "SIGNAL: STANDBY",
                style = PipBoyTypography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = AccentAmber
                )
            )
        }

        // Tuned Station Big Title Row
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(androidx.compose.ui.graphics.Color(0x33000000), RoundedCornerShape(6.dp))
                .border(1.dp, SurfaceBorder.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "> TUNED STATION:",
                style = PipBoyTypography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = PhosphorSecondary
                )
            )

            Text(
                text = if (isPlaying) (currentStation?.name ?: "--- NO SIGNAL ---") else (currentStation?.name ?: "STANDBY // READY"),
                style = PipBoyTypography.titleMedium.copy(
                    color = if (isOverdrive) AccentRed else PhosphorPrimary,
                    fontSize = 15.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.testTag("current_station_name")
            )
        }

        // Diagnostic Metrics Rows
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "STATUS:",
                style = PipBoyTypography.bodyMedium.copy(fontSize = 12.sp)
            )

            Text(
                text = statusSignal,
                style = PipBoyTypography.titleMedium.copy(
                    fontSize = 12.sp,
                    color = if (isAlert) AccentRed else PhosphorPrimary
                ),
                modifier = Modifier
                    .alpha(if (isAlert) blinkAlpha else 1f)
                    .testTag("status_signal_value")
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "RELAY NETWORK:",
                style = PipBoyTypography.bodyMedium.copy(fontSize = 12.sp)
            )

            Text(
                text = "[ ${currentStation?.category?.uppercase() ?: "ALL"} ]",
                style = PipBoyTypography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = AccentAmber
                )
            )
        }

        // Quick Jump to Tuner Button (if callback provided)
        if (onBrowseStations != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, PhosphorSecondary.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                    .background(androidx.compose.ui.graphics.Color(0x221FA85B), RoundedCornerShape(6.dp))
                    .clickable { onBrowseStations() }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "► BROWSE ALL 275+ FREQUENCIES ◄",
                    style = PipBoyTypography.labelSmall.copy(
                        fontSize = 11.sp,
                        color = PhosphorPrimary
                    )
                )
            }
        }
    }
}
