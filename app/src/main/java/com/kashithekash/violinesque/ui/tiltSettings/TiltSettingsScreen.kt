package com.kashithekash.violinesque.ui.tiltSettings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kashithekash.violinesque.ui.components.HorizontalLine
import com.kashithekash.violinesque.ui.components.VerticalLine
import com.kashithekash.violinesque.ui.play.StringsContainer
import com.kashithekash.violinesque.ui.theme.ViolinEsqueTheme
import com.kashithekash.violinesque.utility.ViolinString

@Composable
fun TiltSettingsScreen (
    currentStringLiveData: LiveData<ViolinString>,
    invertRollLiveData: MutableLiveData<Boolean>,
    toggleInvertRoll: () -> Unit,
    invertPitchLiveData: MutableLiveData<Boolean>,
    toggleInvertPitch: () -> Unit,
    setGDRollPoint: () -> Unit,
    setAERollPoint: () -> Unit,
    resetRollPoints: () -> Unit,
    setTiltAwayLimit: () -> Unit,
    setTiltTowardLimit: () -> Unit,
    resetTiltLimits: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column (modifier = modifier
        .fillMaxSize()
        .padding(vertical = 30.dp)
        .padding(end = 30.dp)
    ) {

        // invert Roll
        InvertRollSwitch(
            invertRollLiveData = invertRollLiveData,
            toggleInvertRoll = { toggleInvertRoll() },
            modifier = modifier
                .fillMaxWidth()
                .height(SettingRowHeight)
        )

        HorizontalLine(modifier = modifier)

        // Invert Pitch
        InvertPitchSwitch(
            invertPitchLiveData = invertPitchLiveData,
            toggleInvertPitch = { toggleInvertPitch() },
            modifier = modifier
                .fillMaxWidth()
                .height(SettingRowHeight)
        )

        HorizontalLine(modifier = modifier)

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

        // Reset roll limits
        ResetRollLimitsButton(
            resetRollPoints = { resetRollPoints() },
            modifier = modifier
                .fillMaxWidth()
                .height(SettingRowHeight)
        )

        HorizontalLine(modifier = modifier)

        // Reset pitch limits
        ResetPitchLimitsButton(
            resetTiltLimits = { resetTiltLimits() },
            modifier = modifier
                .fillMaxWidth()
                .height(SettingRowHeight)
        )

        HorizontalLine(modifier = modifier)

        Box (
            contentAlignment = Alignment.CenterStart,
            modifier = modifier
                .fillMaxWidth()
                .height(100.dp)
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
    }
}

@Composable
fun InvertRollSwitch(
    invertRollLiveData: MutableLiveData<Boolean>,
    toggleInvertRoll: () -> Unit,
    modifier: Modifier
) {

    val invertRoll by invertRollLiveData.observeAsState(false)

    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Box (
            contentAlignment = Alignment.CenterStart,
            modifier = modifier
                .fillMaxSize()
                .weight(2f)
        ) {
            Text(
                text = "Invert direction to roll device to change strings",
                color = ViolinEsqueTheme.colors.text
            )
        }

        Box (
            contentAlignment = Alignment.CenterEnd,
            modifier = modifier
                .fillMaxSize()
                .weight(1f)
        ) {

            Switch(
                checked = invertRoll!!,
                onCheckedChange = { toggleInvertRoll() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = ViolinEsqueTheme.colors.textButtonTouched,
                    checkedTrackColor = ViolinEsqueTheme.colors.background,
                    checkedBorderColor = ViolinEsqueTheme.colors.textButtonTouched,
                    uncheckedThumbColor = ViolinEsqueTheme.colors.textAlt,
                    uncheckedTrackColor = ViolinEsqueTheme.colors.background,
                    uncheckedBorderColor = ViolinEsqueTheme.colors.textAlt
                ),
                modifier = modifier.scale(0.8f),
            )
        }
    }
}

@Composable
fun InvertPitchSwitch(
    invertPitchLiveData: MutableLiveData<Boolean>,
    toggleInvertPitch: () -> Unit,
    modifier: Modifier
) {

    val invertPitch by invertPitchLiveData.observeAsState(false)

    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Box (
            contentAlignment = Alignment.CenterStart,
            modifier = modifier
                .fillMaxSize()
                .weight(2f)
        ) {
            Text(
                text = "Invert direction to tilt device to change position",
                color = ViolinEsqueTheme.colors.text
            )
        }

        Box (
            contentAlignment = Alignment.CenterEnd,
            modifier = modifier
                .fillMaxSize()
                .weight(1f)
        ) {

            Switch(
                checked = invertPitch!!,
                onCheckedChange = { toggleInvertPitch() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = ViolinEsqueTheme.colors.textButtonTouched,
                    checkedTrackColor = ViolinEsqueTheme.colors.background,
                    checkedBorderColor = ViolinEsqueTheme.colors.textButtonTouched,
                    uncheckedThumbColor = ViolinEsqueTheme.colors.textAlt,
                    uncheckedTrackColor = ViolinEsqueTheme.colors.background,
                    uncheckedBorderColor = ViolinEsqueTheme.colors.textAlt
                ),
                modifier = modifier.scale(0.8f),
            )
        }
    }
}

@Composable
fun ResetRollLimitsButton(
    resetRollPoints: () -> Unit,
    modifier: Modifier
) {

    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Box (
            contentAlignment = Alignment.CenterStart,
            modifier = modifier
                .fillMaxSize()
                .weight(2f)
        ) {
            Text(
                text = "Invert direction to roll device to change strings",
                color = ViolinEsqueTheme.colors.text
            )
        }

        Box (
            contentAlignment = Alignment.Center,
            modifier = modifier
                .fillMaxSize()
                .weight(1f)
                .clickable() { resetRollPoints() }
        ) {
            Icon(imageVector = Icons.Filled.RotateLeft, contentDescription = "Reset roll points", tint = ViolinEsqueTheme.colors.text)
        }
    }
}

@Composable
fun ResetPitchLimitsButton(
    resetTiltLimits: () -> Unit,
    modifier: Modifier
) {

    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Box (
            contentAlignment = Alignment.CenterStart,
            modifier = modifier
                .fillMaxSize()
                .weight(2f)
        ) {
            Text(
                text = "Reset pitch limits",
                color = ViolinEsqueTheme.colors.text
            )
        }

        Box (
            contentAlignment = Alignment.Center,
            modifier = modifier
                .fillMaxSize()
                .weight(1f)
                .clickable() { resetTiltLimits() }
        ) {
            Icon(imageVector = Icons.Filled.RotateLeft, contentDescription = "Reset tilt limits", tint = ViolinEsqueTheme.colors.text)
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
        modifier = modifier.fillMaxSize()
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

private val SettingRowHeight: Dp = 60.dp