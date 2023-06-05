package com.kashithekash.violinesque.ui.layoutConfig

import android.graphics.Color.alpha
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.kashithekash.violinesque.ui.components.HorizontalLine
import com.kashithekash.violinesque.ui.components.NumberPickerVertical
import com.kashithekash.violinesque.ui.theme.ViolinEsqueTheme

@Composable
fun LayoutConfigScreen (
    expandButtonsLiveData: LiveData<Boolean>,
    onSetExpandButtons: (Boolean) -> Unit,
    handPositionsMutableList: MutableList<Int>,
    addHandPostion: (Int, Int) -> Unit,
    removeHandPosition: (Int) -> Unit,
    changeHandPosition: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier
        .fillMaxSize()
        .padding(vertical = 30.dp)
        .padding(end = 30.dp)
    ) {

        ExpandButtonsSwitch(
            expandButtonsLiveData = expandButtonsLiveData,
            onCheckedChange = { newExpandButtons -> onSetExpandButtons(newExpandButtons) },
            modifier = modifier
                .fillMaxWidth()
                .height(SettingRowHeight)
        )

        HorizontalLine(modifier = modifier)

        Box (
            contentAlignment = Alignment.CenterStart,
            modifier = modifier
                .fillMaxWidth()
                .height(SettingRowHeight)
        ) {

            Text("Available hand positions", color = ViolinEsqueTheme.colors.text)
        }

        Spacer(modifier = modifier.height(20.dp))

        HandPositionCardLazyRow(
            handPositionsMutableList = handPositionsMutableList,
            addHandPostion = { i, n -> addHandPostion(i, n) },
            removeHandPosition = { i -> removeHandPosition(i) },
            changeHandPosition = { i, n -> changeHandPosition(i, n) },
            modifier = modifier
        )

        Spacer(modifier = modifier.height(20.dp))

        HorizontalLine(modifier = modifier)
    }
}

@Composable
fun ExpandButtonsSwitch(
    expandButtonsLiveData: LiveData<Boolean>,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier
) {

    val expandButtons by expandButtonsLiveData.observeAsState(false)

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
                text = "Expand finger position buttons to fill available space",
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
                checked = expandButtons,
                onCheckedChange = { onCheckedChange(!expandButtons) },
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
fun HandPositionCardLazyRow (
    handPositionsMutableList: MutableList<Int>,
    addHandPostion: (Int, Int) -> Unit,
    removeHandPosition: (Int) -> Unit,
    changeHandPosition: (Int, Int) -> Unit,
    modifier: Modifier
) {

    var highestIndex = 0

    LazyRow (userScrollEnabled = true, horizontalArrangement = Arrangement.spacedBy(10.dp)) {

        itemsIndexed(handPositionsMutableList) { index, handPosition ->
            highestIndex = index
            HandPositionCard(
                removeHandPosition = removeHandPosition,
                changeHandPosition = changeHandPosition,
                handPosition = handPosition,
                index = index,
                min = if (index == 0) LowestPosition else handPositionsMutableList[index - 1] + 1,
                max = if (index == handPositionsMutableList.count() - 1) HighestPosition else handPositionsMutableList[index + 1] - 1,
                showDeleteButton = handPositionsMutableList.count() > 1,
                modifier = modifier
            )
        }

        if (handPositionsMutableList.last() < HighestPosition) {
            item {
                AddHandPositionCard(
                    index = highestIndex + 1,
                    handPosition = handPositionsMutableList.last() + 1,
                    addHandPosition = addHandPostion,
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
fun HandPositionCard(
    removeHandPosition: (Int) -> Unit,
    changeHandPosition: (Int, Int) -> Unit,
    handPosition: Int,
    index: Int,
    min: Int,
    max: Int,
    showDeleteButton: Boolean,
    modifier: Modifier
) {

    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(120.dp)
            .wrapContentWidth()
            .border(1.dp, ViolinEsqueTheme.colors.textAlt, shape = RoundedCornerShape(10.dp))
    ) {

        NumberPickerVertical(
            value = handPosition,
            min = min,
            max = max,
            onValueChange = { n -> changeHandPosition(index, n) },
            modifier = modifier
        )

        if (showDeleteButton) {
            IconButton(onClick = { removeHandPosition(index) }) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Remove hand position ${index + 1}.",
                    tint = Color.Red.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun AddHandPositionCard(
    index: Int,
    handPosition: Int,
    addHandPosition: (Int, Int) -> Unit,
    modifier: Modifier
) {

    Box (
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(120.dp)
            .wrapContentWidth()
//            .border(1.dp, ViolinEsqueTheme.colors.textAlt, shape = RoundedCornerShape(5.dp))
    ) {
        IconButton(onClick = { addHandPosition(index, handPosition) }) {
            Icon(
                imageVector = Icons.Outlined.AddCircle,
                contentDescription = "Add hand position to ${index}.",
                tint = ViolinEsqueTheme.colors.textAlt
            )
        }
    }
}

private val SettingRowHeight: Dp = 60.dp
private val LowestPosition: Int = 1
private val HighestPosition: Int = 15