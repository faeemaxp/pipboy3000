package com.robco.pipboyradio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    modifier: Modifier = Modifier
) {
    val borderColor = if (isOverdrive) AccentRed else SurfaceBorder

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 4.dp)
            .testTag("vault_logs_section"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
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
