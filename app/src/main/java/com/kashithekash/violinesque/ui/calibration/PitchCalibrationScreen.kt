package com.kashithekash.violinesque.ui.calibration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kashithekash.violinesque.ui.components.PositionIndicatorRail

@Composable
fun PitchCalibrationScreen(
    handPositionsMutableList: MutableList<Int>,
    currentHandPositionIndexLiveData: LiveData<Int>,
    invertPitchLiveData: MutableLiveData<Boolean>,
    setTiltAwayLimit: () -> Unit,
    setTiltTowardLimit: () -> Unit,
    resetTiltLimits: () -> Unit,
    modifier: Modifier = Modifier
) {

    Row (
        modifier = modifier
            .fillMaxSize()
            .padding(30.dp)
    ) {

        PositionIndicatorRail(
            handPositionsMutableList = handPositionsMutableList,
            currentHandPositionIndexLiveData = currentHandPositionIndexLiveData,
//            invertPitchLiveData = invertPitchLiveData
        )

        Column (
            verticalArrangement = Arrangement.Center,
            modifier = modifier.fillMaxSize()
        ) {


        }
    }
}