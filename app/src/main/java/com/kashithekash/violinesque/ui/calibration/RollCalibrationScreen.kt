package com.kashithekash.violinesque.ui.calibration

import androidx.compose.foundation.Indication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.kashithekash.violinesque.ui.components.HorizontalLine
import com.kashithekash.violinesque.ui.components.VerticalLine
import com.kashithekash.violinesque.ui.play.StringsContainer
import com.kashithekash.violinesque.ui.play.VString
import com.kashithekash.violinesque.ui.theme.ViolinEsqueTheme
import com.kashithekash.violinesque.utility.ViolinString

@Composable
fun RollCalibrationScreen(
    currentStringLiveData: LiveData<ViolinString>,
    setGDRollPoint: () -> Unit,
    setAERollPoint: () -> Unit,
    resetRollPoints: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column (
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(30.dp)
    ) {

        StringsContainer(
            currentStringLiveData = currentStringLiveData,
            modifier = modifier.height(50.dp)
        )

        HorizontalLine(modifier = modifier)

        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {

            Box (contentAlignment = Alignment.Center, modifier = modifier
                .fillMaxHeight()
                .weight(1f)
                .clickable { setGDRollPoint() }
            ) {

                Box (
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
                    .clickable { resetRollPoints() }
            ) {
                Text(text = "RESET", modifier = modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }

            VerticalLine(modifier = modifier)

            Box (contentAlignment = Alignment.Center, modifier = modifier
                .fillMaxHeight()
                .weight(1f)
                .clickable { setAERollPoint() }
            ) {

                Box (
                    modifier = modifier
                        .height(80.dp)
                        .width(2.dp)
                        .background(color = ViolinEsqueTheme.colors.stringActive)
                )
            }
        }
    }
}