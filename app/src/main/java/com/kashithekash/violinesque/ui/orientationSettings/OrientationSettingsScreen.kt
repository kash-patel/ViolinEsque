package com.kashithekash.violinesque.ui.orientationSettings

import android.widget.Toast
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.TripOrigin
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kashithekash.violinesque.ui.components.HorizontalLine
import com.kashithekash.violinesque.ui.components.PositionIndicatorRail
import com.kashithekash.violinesque.ui.components.StringsContainer
import com.kashithekash.violinesque.ui.components.VerticalLine
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
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = modifier.height(30.dp))

                StringsContainer(
                    currentStringLiveData = currentStringLiveData,
                    modifier = modifier.height(30.dp)
                )

                Spacer(modifier = modifier.height(10.dp))

                Column (
                    modifier = modifier
                        .padding(10.dp)
                        .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(10.dp))
                        .wrapContentHeight()
                        .fillMaxWidth()
                ) {

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
                            contentDescription = "Set lowest hand position pitch.",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = modifier.scale(0.5f)
                        )
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Set lowest hand position pitch.",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    HorizontalLine(modifier = modifier)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    ) {

                        Box(contentAlignment = Alignment.Center, modifier = modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .clickable (onClickLabel = "Set G-D string transition roll") { setGDRollPoint() }
                        ) {

                            Box(
                                modifier = modifier
                                    .height(40.dp)
                                    .width(5.dp)
                                    .background(color = MaterialTheme.colorScheme.primary)
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
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        VerticalLine(modifier = modifier)

                        Box(contentAlignment = Alignment.Center, modifier = modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .clickable (onClickLabel = "Set A-E string transition roll") { setAERollPoint() }
                        ) {

                            Box(
                                modifier = modifier
                                    .height(40.dp)
                                    .width(2.dp)
                                    .background(color = MaterialTheme.colorScheme.primary)
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
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            imageVector = Icons.Filled.TripOrigin,
                            contentDescription = "Set tilt toward limit",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = modifier.scale(0.5f)
                        )
                    }
                }
            }
        }
    }
}