package com.kashithekash.violinesque.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DensitySmall
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.SwapVert
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

/*
object AudioSettings : ViolinEsqueDestination {
    override val icon = Icons.Filled.Tune
    override val route = "audio_settings"
}
*/

object OrientationSettings : ViolinEsqueDestination {
    override val icon = Icons.Filled.Settings
    override val route = "orientation_settings"
}

object RollCalibration : ViolinEsqueDestination {
    override val icon = Icons.Filled.SwapHoriz
    override val route = "${OrientationSettings.route}/roll_calibration"
}

object PitchCalibration : ViolinEsqueDestination {
    override val icon = Icons.Filled.SwapVert
    override val route = "${OrientationSettings.route}/pitch_calibration"
}

object YawCalibration : ViolinEsqueDestination {
    override val icon = Icons.Filled.ScreenRotation
    override val route = "${OrientationSettings.route}/yaw_calibration"
}

val violinEsqueScreens = listOf(Play, InterfaceConfig, OrientationSettings)