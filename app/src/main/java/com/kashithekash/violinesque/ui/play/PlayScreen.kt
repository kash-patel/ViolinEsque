package com.kashithekash.violinesque.ui.play

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kashithekash.violinesque.ui.components.PositionIndicatorRail
import com.kashithekash.violinesque.ui.components.StringsContainer
import com.kashithekash.violinesque.ui.theme.ViolinEsqueTheme
import com.kashithekash.violinesque.utility.A_STRING_NOTES
import com.kashithekash.violinesque.utility.D_STRING_NOTES
import com.kashithekash.violinesque.utility.E_STRING_NOTES
import com.kashithekash.violinesque.utility.G_STRING_NOTES
import com.kashithekash.violinesque.utility.ViolinString
import com.kashithekash.violinesque.utility.handPostionStartIndices

@Composable
fun PlayScreen (
    currentStringLiveData: LiveData<ViolinString>,
    currentHandPositionIndexLiveData: LiveData<Int>,
    expandButtonsLiveData: LiveData<Boolean>,
    alignButtonsBottomLiveData: LiveData<Boolean>,
    handPositionsMutableList: MutableList<Int>,
    onButtonTouch: (Int) -> Unit,
    onButtonRelease: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    Row (
        modifier = modifier
            .fillMaxSize()
    ) {

        PositionIndicatorRail(
            handPositionsMutableList = handPositionsMutableList,
            currentHandPositionIndexLiveData = currentHandPositionIndexLiveData
        )

        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
                .weight(1f)
        ) {

            OpenStringButtonContainer(
                currentHandPositionIndexLiveData,
                handPositionsMutableList,
                onButtonTouch = onButtonTouch,
                onButtonRelease = onButtonRelease,
                modifier = modifier
            )

            StringsContainer(currentStringLiveData = currentStringLiveData, modifier = modifier)
        }

        Spacer(modifier = modifier.width(5.dp))

        FingerPositionButtonsContainer(
            currentStringLiveData = currentStringLiveData,
            handPositionsListMutable = handPositionsMutableList,
            currentHandPositionIndexLiveData = currentHandPositionIndexLiveData,
            onButtonTouch = { n -> onButtonTouch(n) },
            onButtonRelease = { n -> onButtonRelease(n) },
            expandButtonsLiveData = expandButtonsLiveData,
            alignButtonsBottomLiveData = alignButtonsBottomLiveData,
            modifier = modifier
        )

    }
}

@Composable
fun OpenStringButtonContainer(
    currentHandPositionIndexLiveData: LiveData<Int>,
    handPositionsListMutable: MutableList<Int>,
    onButtonTouch: (Int) -> Unit,
    onButtonRelease: (Int) -> Unit,
    modifier: Modifier
) {

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        OpenStringButton(
            currentHandPositionIndexLiveData,
            handPositionsListMutable,
            { onButtonTouch(0) },
            { onButtonRelease(0) },
            modifier
        )
    }
}

@Composable
fun OpenStringButton(
    currentHandPositionIndexLiveData: LiveData<Int>,
    handPositionsListMutable: MutableList<Int>,
    onButtonTouch: (Int) -> Unit,
    onButtonRelease: (Int) -> Unit,
    modifier: Modifier
) {

    val currentHandPositionIndex: Int by currentHandPositionIndexLiveData.observeAsState(0)

    var isTouched by remember { mutableStateOf(false) }

    Box(modifier = modifier
        .fillMaxSize()
        .background(color = if (isTouched) MaterialTheme.colorScheme.surface else Color.Transparent)
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    awaitFirstDown()
                    isTouched = true
                    onButtonTouch(0)

                    do {
                        val event = awaitPointerEvent()
                        event.changes.forEach { pointerInputChange: PointerInputChange ->
                            pointerInputChange.consume()
                        }
                    } while (event.changes.any { it.pressed })

                    isTouched = false
                    onButtonRelease(0)
                }
            }
        }
    ) {
        Text(
            text = handPositionsListMutable[currentHandPositionIndex].toString(),
            color = if (isTouched) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            modifier = modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun RowScope.FingerPositionButtonsContainer (
    currentStringLiveData: LiveData<ViolinString>,
    handPositionsListMutable: MutableList<Int>,
    currentHandPositionIndexLiveData: LiveData<Int>,
    onButtonTouch: (Int) -> Unit,
    onButtonRelease: (Int) -> Unit,
    expandButtonsLiveData: LiveData<Boolean>,
    alignButtonsBottomLiveData: LiveData<Boolean>,
    modifier: Modifier
) {

    val expandButtons: Boolean by expandButtonsLiveData.observeAsState(false)
    val alignButtonsBottom: Boolean by alignButtonsBottomLiveData.observeAsState(false)
    val currentHandPositionIndex: Int by currentHandPositionIndexLiveData.observeAsState(0)

    regularButtonHeight = (LocalConfiguration.current.screenHeightDp / 12).dp

    Column (modifier = modifier
        .fillMaxSize()
        .weight(1f),
        verticalArrangement = if (alignButtonsBottom) Arrangement.Bottom else Arrangement.Center
    ) {

        for (i in 1..if (handPositionsListMutable[currentHandPositionIndex] == 15) 6 else 8) {

            FingerPositionButton(
                expandButtons = expandButtons,
                fingerPosition = i,
                currentStringLiveData = currentStringLiveData,
                currentHandPositionIndex = currentHandPositionIndex,
                handPositionsListMutable = handPositionsListMutable,
                onButtonTouch = { n -> onButtonTouch(n) },
                onButtonRelease = { n -> onButtonRelease(n) },
                modifier = modifier
            )
        }
    }

}

@Composable
fun ColumnScope.FingerPositionButton (
    expandButtons: Boolean,
    fingerPosition: Int,
    currentStringLiveData: LiveData<ViolinString>,
    currentHandPositionIndex: Int,
    handPositionsListMutable: MutableList<Int>,
    onButtonTouch: (Int) -> Unit,
    onButtonRelease: (Int) -> Unit,
    modifier: Modifier
) {

    val currentString: ViolinString by currentStringLiveData.observeAsState(ViolinString.A)

    val currentHandPosition = handPositionsListMutable[currentHandPositionIndex]
    val buttonNumber = handPostionStartIndices[currentHandPosition] + fingerPosition

    var isTouched by remember { mutableStateOf(false) }

    Box(modifier = modifier
        .then(
            if (expandButtons)
                Modifier
                    .fillMaxSize()
                    .weight(1f)
            else
                Modifier
                    .fillMaxWidth()
                    .height(regularButtonHeight)
        )
        .fillMaxWidth()
        .height(regularButtonHeight)
        .background(color = if (isTouched) MaterialTheme.colorScheme.surface else Color.Transparent)
        .pointerInput(Unit) {

            forEachGesture {

                awaitPointerEventScope {

                    awaitFirstDown()
                    isTouched = true
                    onButtonTouch(fingerPosition)

                    do {
                        val event = awaitPointerEvent()
                        event.changes.forEach { pointerInputChange: PointerInputChange ->
                            pointerInputChange.consume()
                        }
                    } while (event.changes.any { it.pressed })

                    isTouched = false
                    onButtonRelease(fingerPosition)
                }
            }
        }
    ) {
        val noteString = when (currentString) {
            ViolinString.G -> G_STRING_NOTES[buttonNumber]
            ViolinString.D -> D_STRING_NOTES[buttonNumber]
            ViolinString.A -> A_STRING_NOTES[buttonNumber]
            ViolinString.E -> E_STRING_NOTES[buttonNumber]
        }

        Text(
            text = noteString,
            color = if (isTouched) MaterialTheme.colorScheme.primary
            else if (fingerPosition == 1
                    || fingerPosition == 3
                    || fingerPosition == 6
                    || fingerPosition == 8
                ) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 15.dp)
        )
    }
}

private var regularButtonHeight: Dp = 0.dp