package com.kashithekash.violinesque.ui.play

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
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
    handPositionsListMutable: MutableList<Int>,
    onButtonTouch: (Int) -> Unit,
    onButtonRelease: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    Row (
        modifier = modifier
            .fillMaxSize()
    ) {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
                .weight(1f)
        ) {

            OpenStringButtonContainer(
                currentHandPositionIndexLiveData,
                handPositionsListMutable,
                onButtonTouch = onButtonTouch,
                onButtonRelease = onButtonRelease,
                modifier = modifier
            )

            StringsContainer(currentStringLiveData = currentStringLiveData, modifier = modifier)
        }

        Spacer(modifier = modifier.width(5.dp))

        FingerPositionButtonsContainer(
            currentStringLiveData = currentStringLiveData,
            handPositionsListMutable = handPositionsListMutable,
            currentHandPositionIndexLiveData = currentHandPositionIndexLiveData,
            onButtonTouch = { n -> onButtonTouch(n) },
            onButtonRelease = { n -> onButtonRelease(n) },
            expandButtonsLiveData = expandButtonsLiveData,
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
        .background(color = if (isTouched) ViolinEsqueTheme.colors.fingerBoardTouched else ViolinEsqueTheme.colors.fingerBoard)
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
            color = if (isTouched) ViolinEsqueTheme.colors.textButtonTouched else ViolinEsqueTheme.colors.textButton,
            modifier = modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun StringsContainer (currentStringLiveData: LiveData<ViolinString>, modifier: Modifier) {

    val currentString : ViolinString by currentStringLiveData.observeAsState(ViolinString.A)

    Row (
        modifier = modifier
            .fillMaxSize()
    ) {
        VString(isHighlighted = currentString == ViolinString.G, width = 5.dp, modifier = modifier)
        VString(isHighlighted = currentString == ViolinString.D, width = 4.dp, modifier = modifier)
        VString(isHighlighted = currentString == ViolinString.A, width = 3.dp, modifier = modifier)
        VString(isHighlighted = currentString == ViolinString.E, width = 2.dp, modifier = modifier)
    }
}

@Composable
fun RowScope.VString (isHighlighted: Boolean, width: Dp, modifier: Modifier) {

    Box (
        modifier
            .fillMaxSize()
            .weight(1f)
    ) {
        Box(modifier = modifier
            .align(Alignment.Center)
            .width(width)
            .fillMaxHeight()
            .background(color = if (isHighlighted) ViolinEsqueTheme.colors.stringActive else ViolinEsqueTheme.colors.string)
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
    modifier: Modifier
) {

    val expandButtons: Boolean by expandButtonsLiveData.observeAsState(false)

    regularButtonHeight = (LocalConfiguration.current.screenHeightDp / 12).dp

    Column (modifier = modifier
        .fillMaxSize()
        .weight(1f)
    ) {

        if (!expandButtons) {
            Spacer(modifier = modifier
                .fillMaxWidth()
                .height(regularButtonHeight * 2)
            )
        }

        Column (modifier = modifier
            .fillMaxSize()
            .weight(1f)
        ) {

            for (i in 1..8) {

                FingerPositionButton(
                    fingerPosition = i,
                    currentStringLiveData = currentStringLiveData,
                    currentHandPositionIndexLiveData = currentHandPositionIndexLiveData,
                    handPositionsListMutable = handPositionsListMutable,
                    onButtonTouch = { n -> onButtonTouch(n) },
                    onButtonRelease = { n -> onButtonRelease(n) },
                    modifier = modifier
                )
            }
        }

        if (!expandButtons) {
            Spacer(modifier = modifier
                .fillMaxWidth()
                .height(regularButtonHeight * 2)
            )
        }
    }

}

@Composable
fun ColumnScope.FingerPositionButton (
    fingerPosition: Int,
    currentStringLiveData: LiveData<ViolinString>,
    currentHandPositionIndexLiveData: LiveData<Int>,
    handPositionsListMutable: MutableList<Int>,
    onButtonTouch: (Int) -> Unit,
    onButtonRelease: (Int) -> Unit,
    modifier: Modifier
) {

    val currentString: ViolinString by currentStringLiveData.observeAsState(ViolinString.A)
    val currentHandPositionIndex by currentHandPositionIndexLiveData.observeAsState(0)

    val currentHandPosition = handPositionsListMutable[currentHandPositionIndex]
    val buttonNumber = handPostionStartIndices[currentHandPosition] + fingerPosition

    var isTouched by remember { mutableStateOf(false) }

    Box(modifier = modifier
        .fillMaxSize()
        .weight(1f)
        .background(color = if (isTouched) ViolinEsqueTheme.colors.buttonTouched else ViolinEsqueTheme.colors.button)
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
            color = if (isTouched) ViolinEsqueTheme.colors.textButtonTouched
            else if (fingerPosition == 1
                    || fingerPosition == 3
                    || fingerPosition == 6
                    || fingerPosition == 8
                ) ViolinEsqueTheme.colors.textAlt
            else ViolinEsqueTheme.colors.textButton,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 15.dp)
        )
    }
}

private var regularButtonHeight: Dp = 0.dp