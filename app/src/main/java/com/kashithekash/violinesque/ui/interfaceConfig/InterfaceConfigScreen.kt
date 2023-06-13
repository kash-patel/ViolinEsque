package com.kashithekash.violinesque.ui.interfaceConfig

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.kashithekash.violinesque.ui.components.HorizontalLine
import com.kashithekash.violinesque.ui.components.NumberPickerHorizontal
import com.kashithekash.violinesque.ui.components.SettingsRowSwitch
import com.kashithekash.violinesque.ui.theme.ViolinEsqueTheme

@Composable
fun InterfaceConfigScreen (
    expandButtonsLiveData: LiveData<Boolean>,
    onSetExpandButtons: (Boolean) -> Unit,
    alignButtonsBottomLiveData: LiveData<Boolean>,
    onSetAlignButtonsBottom: (Boolean) -> Unit,
    handPositionsMutableList: MutableList<Int>,
    addHandPosition: (Int, Int) -> Unit,
    removeHandPosition: (Int) -> Unit,
    changeHandPosition: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier
        .fillMaxSize()
        .padding(vertical = 30.dp)
        .padding(end = 30.dp)
    ) {

        SettingsRowSwitch(
            text = "Expand finger position buttons to fill all available space",
            booleanStateLiveData = expandButtonsLiveData,
            onCheckedChange = { newExpandButtons -> onSetExpandButtons(newExpandButtons) },
            modifier = modifier
        )

        HorizontalLine(modifier = modifier)

        SettingsRowSwitch(
            text = "Align finger position buttons to bottom of screen",
            booleanStateLiveData = alignButtonsBottomLiveData,
            onCheckedChange = { newAlignButtonsBottom -> onSetAlignButtonsBottom(newAlignButtonsBottom) },
            modifier = modifier
        )

        HorizontalLine(modifier = modifier)

        Box (
            contentAlignment = Alignment.Center,
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 5.dp)
        ) {
            Text(
                "Available Hand Positions",
                color = ViolinEsqueTheme.colors.text,
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Spacer(modifier = modifier.height(20.dp))

        HandPositionCardLazyColumn(
            handPositionsMutableList = handPositionsMutableList,
            addHandPosition = { i, n -> addHandPosition(i, n) },
            removeHandPosition = { i -> removeHandPosition(i) },
            changeHandPosition = { i, n -> changeHandPosition(i, n) },
            modifier = modifier
        )

        Spacer(modifier = modifier.height(20.dp))

        HorizontalLine(modifier = modifier)
    }
}

@Composable
fun HandPositionCardLazyColumn (
    handPositionsMutableList: MutableList<Int>,
    addHandPosition: (Int, Int) -> Unit,
    removeHandPosition: (Int) -> Unit,
    changeHandPosition: (Int, Int) -> Unit,
    modifier: Modifier
) {

    var highestIndex = 0

    LazyColumn (userScrollEnabled = true, verticalArrangement = Arrangement.spacedBy(10.dp)) {

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
                    addHandPosition = addHandPosition,
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
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = ViolinEsqueTheme.colors.buttonTouched, shape = RoundedCornerShape(10.dp))
            .padding(horizontal = 5.dp)
            .padding(vertical = 10.dp),
        horizontalArrangement = if (showDeleteButton) Arrangement.SpaceBetween else Arrangement.Center
    ) {

        NumberPickerHorizontal(
            value = handPosition,
            range = (min..max).toList(),
            onValueChange = { n -> changeHandPosition(index, n) },
            modifier = modifier
        )

        if (showDeleteButton) {
            IconButton(onClick = { removeHandPosition(index) }, modifier = modifier.wrapContentWidth()) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Remove hand position ${index + 1}.",
                    tint = Color.Red.copy(red = 0.5f)
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
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        IconButton(onClick = { addHandPosition(index, handPosition) }) {
            Icon(
                imageVector = Icons.Outlined.AddCircle,
                contentDescription = "Add hand position to ${index}.",
                tint = ViolinEsqueTheme.colors.text
            )
        }
    }
}

private val SettingRowHeight: Dp = 80.dp
private const val LowestPosition: Int = 1
private const val HighestPosition: Int = 15