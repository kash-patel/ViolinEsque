package com.kashithekash.violinesque.ui.orientationSettings

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kashithekash.violinesque.navigateSingleTopTo
import com.kashithekash.violinesque.navigation.OrientationSettings
import com.kashithekash.violinesque.navigation.PitchCalibration
import com.kashithekash.violinesque.navigation.RollCalibration
import com.kashithekash.violinesque.ui.calibration.PitchCalibrationScreen
import com.kashithekash.violinesque.ui.calibration.RollCalibrationScreen
import com.kashithekash.violinesque.ui.components.HorizontalLine
import com.kashithekash.violinesque.ui.components.SettingsRowButton
import com.kashithekash.violinesque.ui.components.SettingsRowSwitch
import com.kashithekash.violinesque.ui.theme.ViolinEsqueTheme
import com.kashithekash.violinesque.utility.ViolinString

@Composable
fun OrientationSettingsScreen (
    invertRollLiveData: MutableLiveData<Boolean>,
    toggleInvertRoll: () -> Unit,
    invertPitchLiveData: MutableLiveData<Boolean>,
    toggleInvertPitch: () -> Unit,
    goToRollCalibrationScreen: () -> Unit,
    goToPitchCalibrationScreen: () -> Unit,
    goToYawCalibrationScreen: () -> Unit,
    modifier: Modifier = Modifier
) {

    BoxWithConstraints(modifier = modifier
        .fillMaxSize()
    ) {

        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .padding(vertical = 30.dp)
                .padding(end = 30.dp)
        ) {

            // invert Roll
            SettingsRowSwitch(
                text = "Invert string change direction",
                booleanStateLiveData = invertRollLiveData,
                onCheckedChange = { toggleInvertRoll() },
                modifier = modifier
            )

            HorizontalLine(modifier = modifier)

            // Invert Pitch
            SettingsRowSwitch(
                text = "Invert hand position change direction",
                booleanStateLiveData = invertPitchLiveData,
                onCheckedChange = { toggleInvertPitch() },
                modifier = modifier
            )

            HorizontalLine(modifier = modifier)

            /*
            Box (modifier = modifier
                .fillMaxWidth()
                .height(SettingRowHeight)
            ) {

                StringsContainer(
                    currentStringLiveData = currentStringLiveData,
                    modifier = modifier
                )
            }

            HorizontalLine(modifier = modifier)
            */

            /*
            // Reset roll limits
            SettingsRowButton(
                text = "Reset roll range",
                onClick = { resetRollPoints() },
                icon = Icons.Filled.Refresh,
                modifier = modifier
            )

            HorizontalLine(modifier = modifier)

            // Reset pitch limits
            SettingsRowButton(
                text = "Reset pitch range",
                onClick = { resetTiltLimits() },
                icon = Icons.Filled.Refresh,
                modifier = modifier
            )

            HorizontalLine(modifier = modifier)

            Box (
                contentAlignment = Alignment.CenterStart,
                modifier = modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 5.dp)
            ) {
                Text(
                    text = "Use the buttons below to set the roll limits (left and right buttons) and tilt limits (upper and lower buttons).",
                    color = ViolinEsqueTheme.colors.text
                )
            }

            OrientationLimitButtons(
                setAERollPoint = { setAERollPoint() },
                setGDRollPoint = { setGDRollPoint() },
                setTiltAwayLimit = { setTiltAwayLimit() },
                setTiltTowardLimit = { setTiltTowardLimit() },
                modifier = modifier
            )
            // Set toward and away pitch limits
            // Set CCW and CW roll limits
            */

            SettingsRowButton(
                text = "Set roll limits",
                onClick = { goToRollCalibrationScreen() },
                icon = RollCalibration.icon,
                modifier = modifier
            )

            HorizontalLine(modifier = modifier)

            SettingsRowButton(
                text = "Set pitch limits",
                onClick = { goToPitchCalibrationScreen() },
                icon = PitchCalibration.icon,
                modifier = modifier
            )
        }
    }
}

@Composable
fun OrientationLimitButtons(
    setAERollPoint: () -> Unit,
    setGDRollPoint: () -> Unit,
    setTiltAwayLimit: () -> Unit,
    setTiltTowardLimit: () -> Unit,
    modifier: Modifier
) {

    Box (
        contentAlignment = Alignment.Center,
        modifier = modifier.width(150.dp)
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.size(150.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .height(50.dp)
                    .fillMaxWidth()
            ) {

                Spacer(modifier = modifier.width(50.dp))

                IconButton(
                    onClick = { setTiltAwayLimit() },
                    modifier = modifier
                        .border(1.dp, ViolinEsqueTheme.colors.text, shape = CircleShape)
                        .size(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Set tilt away limit",
                        tint = ViolinEsqueTheme.colors.text
                    )
                }

                Spacer(modifier = modifier.width(50.dp))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .height(50.dp)
                    .fillMaxWidth()
            ) {

                IconButton(
                    onClick = { setGDRollPoint() },
                    modifier = modifier
                        .border(1.dp, ViolinEsqueTheme.colors.text, shape = CircleShape)
                        .size(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowRight,
                        contentDescription = "Set GD roll point",
                        tint = ViolinEsqueTheme.colors.text
                    )
                }

                Spacer(modifier = modifier.width(50.dp))

                IconButton(
                    onClick = { setAERollPoint() },
                    modifier = modifier
                        .border(1.dp, ViolinEsqueTheme.colors.text, shape = CircleShape)
                        .size(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowLeft,
                        contentDescription = "Set AE roll point",
                        tint = ViolinEsqueTheme.colors.text
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .height(50.dp)
                    .fillMaxWidth()
            ) {

                Spacer(modifier = modifier.width(50.dp))

                IconButton(
                    onClick = { setTiltTowardLimit() },
                    modifier = modifier
                        .border(1.dp, ViolinEsqueTheme.colors.text, shape = CircleShape)
                        .size(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowUp,
                        contentDescription = "Set tilt toward limit",
                        tint = ViolinEsqueTheme.colors.text
                    )
                }

                Spacer(modifier = modifier.width(50.dp))
            }
        }
    }
}