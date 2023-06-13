package com.kashithekash.violinesque.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.TripOrigin
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kashithekash.violinesque.ui.theme.ViolinEsqueTheme

@Composable
fun PositionIndicatorRail (
    handPositionsMutableList: MutableList<Int>,
    currentHandPositionIndexLiveData: LiveData<Int>,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier.width(PositionIndicatorRailWidth)) {

        Spacer(modifier = modifier
            .fillMaxHeight()
            .weight(1f)
        )

        for (i in 0 until handPositionsMutableList.count()) {
            PositionIndicatorDot(
                index = i,
                currentHandPositionIndexLiveData = currentHandPositionIndexLiveData,
                modifier = modifier
            )
        }

        Spacer(modifier = modifier
            .fillMaxHeight()
            .weight(1f)
        )
    }
}

@Composable
private fun PositionIndicatorDot(
    index: Int,
    currentHandPositionIndexLiveData: LiveData<Int>,
    modifier: Modifier
) {

    val currentHandPositionIndex by currentHandPositionIndexLiveData.observeAsState(0)

    Box(
        modifier = modifier
            .padding(vertical = 10.dp)
            .padding(end = 10.dp)
            .fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Filled.TripOrigin,
            contentDescription = "Current position is or is not $index",
            tint = if (index == currentHandPositionIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            modifier = modifier.fillMaxWidth()
        )
    }
}

private val PositionIndicatorRailWidth: Dp = 20.dp