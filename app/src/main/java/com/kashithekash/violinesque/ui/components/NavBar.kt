package com.kashithekash.violinesque.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kashithekash.violinesque.navigation.InterfaceConfig
import com.kashithekash.violinesque.navigation.OrientationSettings
import com.kashithekash.violinesque.navigation.PitchCalibration
import com.kashithekash.violinesque.navigation.Play
import com.kashithekash.violinesque.navigation.RollCalibration
import com.kashithekash.violinesque.navigation.ViolinEsqueDestination
import com.kashithekash.violinesque.navigation.YawCalibration
import com.kashithekash.violinesque.ui.theme.ViolinEsqueTheme

@Composable
fun NavBar (
    allScreens: List<ViolinEsqueDestination>,
    onTabSelect: (ViolinEsqueDestination) -> Unit,
    currentScreen: ViolinEsqueDestination
) {
    Column (Modifier.selectableGroup().width(NavBarWidth)) {
        Spacer(
            Modifier
                .fillMaxHeight()
                .weight(1f)
        )
        allScreens.forEach { screen ->
            NavBarTab(
                text = screen.route,
                icon = screen.icon,
                onSelect = { onTabSelect(screen) },
                isSelected = currentScreen == screen
            )
        }
        Spacer(
            Modifier
                .fillMaxHeight()
                .weight(1f)
        )
    }
}

@Composable
fun NavBarTab(
    text: String,
    icon: ImageVector,
    onSelect: () -> Unit,
    isSelected: Boolean
) {
    val durationMilliseconds = if (isSelected) TabFadeInAnimationDuration else TabFadeOutAnimationDuration
    val animSpec = remember {
        tween<Color> (
            durationMillis = durationMilliseconds,
            easing = LinearEasing,
            delayMillis = TabFadeInAnimationDelay
        )
    }
    val tabTintColor by animateColorAsState(
        targetValue = if (isSelected) ViolinEsqueTheme.colors.textButtonTouched else ViolinEsqueTheme.colors.textAlt,
        animationSpec = animSpec
    )
    
    Box(modifier = Modifier
        .padding(horizontal = 10.dp)
        .padding(vertical = 20.dp)
        .animateContentSize()
        .fillMaxWidth()
        .selectable(
            selected = isSelected,
            onClick = onSelect,
            role = Role.Tab,
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(
                bounded = false,
                radius = Dp.Unspecified,
                color = Color.Unspecified
            )
        )
        .clearAndSetSemantics { contentDescription = text }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = tabTintColor,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private val NavBarWidth = 50.dp

private const val TabFadeInAnimationDuration = 150
private const val TabFadeInAnimationDelay = 100
private const val TabFadeOutAnimationDuration = 100