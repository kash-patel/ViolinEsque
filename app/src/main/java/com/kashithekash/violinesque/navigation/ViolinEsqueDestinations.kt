package com.kashithekash.violinesque.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DensitySmall
import androidx.compose.material.icons.filled.EdgesensorHigh
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Tune
import androidx.compose.ui.graphics.vector.ImageVector

interface ViolinEsqueDestination {
    val icon: ImageVector
    val route: String
}

object Play : ViolinEsqueDestination {
    override val icon = Icons.Filled.MusicNote
    override val route = "play"
}

object LayoutConfig : ViolinEsqueDestination {
    override val icon = Icons.Filled.DensitySmall
    override val route = "layout_config"
}

object TiltSettings : ViolinEsqueDestination {
    override val icon = Icons.Filled.Tune
    override val route = "tilt_settings"
}

val violinEsqueScreens = listOf(Play, LayoutConfig, TiltSettings)