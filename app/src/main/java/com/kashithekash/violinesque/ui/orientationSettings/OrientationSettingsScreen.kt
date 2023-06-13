package com.kashithekash.violinesque.ui.orientationSettings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.TripOrigin
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kashithekash.violinesque.ui.components.HorizontalLine
import com.kashithekash.violinesque.ui.components.PositionIndicatorRail
import com.kashithekash.violinesque.ui.components.VerticalLine
import com.kashithekash.violinesque.ui.play.StringsContainer
import com.kashithekash.violinesque.ui.theme.ViolinEsqueTheme
import com.kashithekash.violinesque.utility.ViolinString

@Composable
fun OrientationSettingsScreen (
    currentStringLiveData: LiveData<ViolinString>,
    handPositionsMutableList: MutableList<Int>,
    currentHandPositionIndexLiveData: LiveData<Int>,
    setGDRollPoint: () -> Unit,
    setAERollPoint: () -> Unit,
    resetRollPoints: () -> Unit,
    setLowestPositionPitch: () -> Unit,
    setHighestPositionPitch: () -> Unit,
    resetPitchLimits: () -> Unit,
    modifier: Modifier = Modifier
) {

    BoxWithConstraints(modifier = modifier
        .fillMaxSize()
    ) {

        Row (
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxSize()
                .padding(vertical = 30.dp)
                .padding(end = 50.dp)
        ) {

            PositionIndicatorRail(
                handPositionsMutableList = handPositionsMutableList,
                currentHandPositionIndexLiveData = currentHandPositionIndexLiveData,
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = modifier.fillMaxSize()
            ) {

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = modifier.padding(vertical = 5.dp)
                ) {
                    Text(
                        text = "Here you can set how far clockwise or counterclockwise you want to roll your device to reach the G or E string, and how far up or down you want to pitch your device to reach the lowest or highest hand position.",
                        modifier = modifier.fillMaxWidth(),
                        color = ViolinEsqueTheme.colors.text
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = modifier.padding(vertical = 5.dp)
                ) {
                    Text(
                        text = "Live indicators are shown so you can see how your settings change your experience in real time.",
                        modifier = modifier.fillMaxWidth(),
                        color = ViolinEsqueTheme.colors.text
                    )
                }

                Spacer(modifier = modifier.height(30.dp))

                StringsContainer(
                    currentStringLiveData = currentStringLiveData,
                    modifier = modifier.height(50.dp)
                )

                HorizontalLine(modifier = modifier)

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = modifier
                        .height(80.dp)
                        .fillMaxWidth()
                        .clickable { setHighestPositionPitch() },
                ) {
                    Icon(
                        imageVector = Icons.Filled.TripOrigin,
                        contentDescription = "Set tilt away limit",
                        tint = ViolinEsqueTheme.colors.stringActive,
                        modifier = modifier.scale(0.5f)
                    )
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "Set tilt away limit",
                        tint = ViolinEsqueTheme.colors.stringActive
                    )
                }

                HorizontalLine(modifier = modifier)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier
                        .fillMaxWidth()
                        .height(100.dp)
                ) {

                    Box(contentAlignment = Alignment.Center, modifier = modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .clickable { setGDRollPoint() }
                    ) {

                        Box(
                            modifier = modifier
                                .height(80.dp)
                                .width(5.dp)
                                .background(color = ViolinEsqueTheme.colors.stringActive)
                        )
                    }

                    VerticalLine(modifier = modifier)

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable {
                                resetRollPoints()
                                resetPitchLimits()
                            }
                    ) {
                        Text(
                            text = "RESET",
                            modifier = modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = ViolinEsqueTheme.colors.text
                        )
                    }

                    VerticalLine(modifier = modifier)

                    Box(contentAlignment = Alignment.Center, modifier = modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .clickable { setAERollPoint() }
                    ) {

                        Box(
                            modifier = modifier
                                .height(80.dp)
                                .width(2.dp)
                                .background(color = ViolinEsqueTheme.colors.stringActive)
                        )
                    }
                }

                HorizontalLine(modifier = modifier)

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = modifier
                        .height(80.dp)
                        .fillMaxWidth()
                        .clickable { setLowestPositionPitch() },
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "Set tilt toward limit",
                        tint = ViolinEsqueTheme.colors.stringActive
                    )
                    Icon(
                        imageVector = Icons.Filled.TripOrigin,
                        contentDescription = "Set tilt toward limit",
                        tint = ViolinEsqueTheme.colors.stringActive,
                        modifier = modifier.scale(0.5f)
                    )
                }
            }
        }
    }
}