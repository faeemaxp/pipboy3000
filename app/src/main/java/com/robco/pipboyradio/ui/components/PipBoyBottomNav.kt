package com.robco.pipboyradio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robco.pipboyradio.data.model.PipBoyTab
import com.robco.pipboyradio.ui.theme.AccentRed
import com.robco.pipboyradio.ui.theme.PhosphorPrimary
import com.robco.pipboyradio.ui.theme.PhosphorSecondary
import com.robco.pipboyradio.ui.theme.PipBoyTypography
import com.robco.pipboyradio.ui.theme.ScreenBackground
import com.robco.pipboyradio.ui.theme.SurfaceBorder

@Composable
fun PipBoyBottomNav(
    currentTab: PipBoyTab,
    isOverdrive: Boolean,
    onTabSelected: (PipBoyTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isOverdrive) AccentRed else SurfaceBorder

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(ScreenBackground)
            .border(
                width = 1.dp,
                color = borderColor.copy(alpha = 0.6f)
            )
            .navigationBarsPadding()
            .testTag("pipboy_bottom_nav")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                title = "RECEIVER",
                icon = Icons.Default.GraphicEq,
                isSelected = currentTab == PipBoyTab.RECEIVER,
                isOverdrive = isOverdrive,
                onClick = { onTabSelected(PipBoyTab.RECEIVER) },
                testTag = "nav_tab_receiver",
                modifier = Modifier.weight(1f)
            )

            NavItem(
                title = "TUNER",
                icon = Icons.Default.List,
                isSelected = currentTab == PipBoyTab.TUNER,
                isOverdrive = isOverdrive,
                onClick = { onTabSelected(PipBoyTab.TUNER) },
                testTag = "nav_tab_tuner",
                modifier = Modifier.weight(1f)
            )

            NavItem(
                title = "SETTINGS",
                icon = Icons.Default.Settings,
                isSelected = currentTab == PipBoyTab.SETTINGS,
                isOverdrive = isOverdrive,
                onClick = { onTabSelected(PipBoyTab.SETTINGS) },
                testTag = "nav_tab_settings",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun NavItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    isOverdrive: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val activeColor = if (isOverdrive) AccentRed else PhosphorPrimary
    val inactiveColor = PhosphorSecondary.copy(alpha = 0.5f)

    Box(
        modifier = modifier
            .height(52.dp)
            .padding(horizontal = 4.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) activeColor else inactiveColor,
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = if (isSelected) "[ $title ]" else title,
                style = PipBoyTypography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = if (isSelected) activeColor else inactiveColor,
                    letterSpacing = 1.sp
                ),
                maxLines = 1
            )
        }
    }
}
