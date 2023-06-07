package com.kashithekash.violinesque.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DensitySmall
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerticalSplit
import androidx.compose.ui.graphics.vector.ImageVector

interface ViolinEsqueDestination {
    val icon: ImageVector
    val route: String
}

object Play : ViolinEsqueDestination {
    override val icon = Icons.Filled.MusicNote
    override val route = "play"
}

object InterfaceConfig : ViolinEsqueDestination {
    override val icon = Icons.Filled.DensitySmall
    override val route = "interface_config"
}

object AudioSettings : ViolinEsqueDestination {
    override val icon = Icons.Filled.Tune
    override val route = "audio_settings"
}

object Calibration : ViolinEsqueDestination {
    override val icon = Icons.Filled.Settings
    override val route = "calibration"
}

val violinEsqueScreens = listOf(Play, InterfaceConfig, AudioSettings, Calibration)