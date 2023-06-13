package com.kashithekash.violinesque.ui.audioSettings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kashithekash.violinesque.ui.components.HorizontalLine
import com.kashithekash.violinesque.ui.components.IntegerSlider
import com.kashithekash.violinesque.viewmodels.MaxBlendTime
import com.kashithekash.violinesque.viewmodels.MaxFadeInTime
import com.kashithekash.violinesque.viewmodels.MaxFadeOutDelay
import com.kashithekash.violinesque.viewmodels.MaxFadeOutTime
import com.kashithekash.violinesque.viewmodels.MinBlendTime
import com.kashithekash.violinesque.viewmodels.MinFadeOutDelay
import com.kashithekash.violinesque.viewmodels.MinFadeOutTime
import com.kashithekash.violinesque.viewmodels.MinFadeInTime

@Composable
fun AudioSettingsScreen(
    getFadeInTime: () -> Int,
    onFadeInTimeChange: (Int) -> Unit,
    getBlendTime: () -> Int,
    onBlendTimeChange: (Int) -> Unit,
    getFadeOutTime: () -> Int,
    onFadeOutTimeChange: (Int) -> Unit,
    getFadeOutDelay: () -> Int,
    onFadeOutDelayChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier
        .fillMaxSize()
        .padding(vertical = 30.dp)
        .padding(end = 30.dp)
    ) {

        IntegerSlider(
            text = "Note fade-in time (milliseconds)",
            initialValue = getFadeInTime(),
            min = MinFadeInTime,
            max = MaxFadeInTime,
            step = 10,
            onValueChange = { newFadeInTime -> onFadeInTimeChange(newFadeInTime) },
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 5.dp)
        )

        HorizontalLine(modifier = modifier)

        IntegerSlider(
            text = "Note blend time (milliseconds)",
            initialValue = getBlendTime(),
            min = MinBlendTime,
            max = MaxBlendTime,
            step = 10,
            onValueChange = { newBlendTime -> onBlendTimeChange(newBlendTime) },
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 5.dp)
        )

        HorizontalLine(modifier = modifier)

        IntegerSlider(
            text = "Note fade-out time (milliseconds)",
            initialValue = getFadeOutTime(),
            min = MinFadeOutTime,
            max = MaxFadeOutTime,
            step = 10,
            onValueChange = { newFadeOutTime -> onFadeOutTimeChange(newFadeOutTime) },
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 5.dp)
        )

        HorizontalLine(modifier = modifier)

        IntegerSlider(
            text = "Note fade-out delay (milliseconds)",
            initialValue = getFadeOutDelay(),
            min = MinFadeOutDelay,
            max = MaxFadeOutDelay,
            step = 10,
            onValueChange = { newFadeOutDelay -> onFadeOutDelayChange(newFadeOutDelay) },
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 5.dp)
        )
    }
}