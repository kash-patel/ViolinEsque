package com.kashithekash.violinesque

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.kashithekash.violinesque.ui.theme.ViolinEsqueTheme

private val G_STRING_NOTES: Array<String> =
    arrayOf("G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G")
private val D_STRING_NOTES: Array<String> =
    arrayOf("D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "C", "C#", "D")
private val A_STRING_NOTES: Array<String> =
    arrayOf("A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A")
private val E_STRING_NOTES: Array<String> =
    arrayOf("E", "F", "F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E")

class PlayModeActivity : ComponentActivity() {

    private lateinit var playModeActivityViewModel: PlayModeActivityViewModel
    private val configIterationLiveData: MutableLiveData<Int> = MutableLiveData(0)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        playModeActivityViewModel = ViewModelProvider(this)[PlayModeActivityViewModel(application)::class.java]

        playModeActivityViewModel.loadConfig()
        playModeActivityViewModel.monitorStrings()

        playModeActivityViewModel.getRotationVector().observe(this) {
            playModeActivityViewModel.updateRoll(it[2])
        }

        setContent {
            ViolinEsqueTheme {

                val configUpdateNum = configIterationLiveData.observeAsState(0)
                Log.w("PlayMode", "Config iteration ${configUpdateNum.value}.")

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ViolinEsqueTheme.colors.background
                ) {
                    PlayMode(
                        configIterationLiveData,
                        playModeActivityViewModel.getCurrentStringLiveData(),
                        { n -> playModeActivityViewModel.buttonTouched(n) },
                        { n -> playModeActivityViewModel.buttonReleased(n) },
                        { n -> playModeActivityViewModel.getButtonInteractability(n) }
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        configIterationLiveData.value = configIterationLiveData.value!!.plus(1)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        playModeActivityViewModel.releaseResources()
    }
}

@Composable
fun PlayMode (
    configIterationLiveData: LiveData<Int>,
    currentStringLiveData: LiveData<ViolinString>,
    onButtonTouched: (Int) -> Unit,
    onButtonReleased: (Int) -> Unit,
    getButtonInteractability: (Int) -> Interactability,
    modifier: Modifier = Modifier
) {
    Log.w("PlayMode", "Config changed!")

    Column (modifier = modifier
        .fillMaxSize()
    ) {
        Spacer(modifier = modifier.height(5.dp))
        Row (modifier = modifier
            .fillMaxSize()
            .weight(weight = 1f)
        ) {
            SideBar()
            Spacer(modifier = modifier.width(10.dp))
            OpenStringButtonContainer(
                currentStringLiveData,
                { onButtonTouched(0) },
                { onButtonReleased(0) },
                getButtonInteractability(0) == Interactability.ENABLED,
                modifier
            )
            Spacer(modifier = modifier.width(5.dp))
            FingerPositionButtonsContainer(
//                configIterationLiveData,
                { n -> getButtonInteractability(n) },
                currentStringLiveData,
                { n -> onButtonTouched(n) },
                { n -> onButtonReleased(n) },
                modifier
            )
        }
    }
}

@Composable
fun SideBar (modifier: Modifier = Modifier) {

    Column (
        modifier = modifier
            .width(50.dp)
    ) {
        Spacer(modifier = modifier.weight(1f))
        LayoutConfigButton(modifier = modifier)
        Spacer(modifier = modifier.height(30.dp))
        CalibrationSettingsButton(modifier = modifier)
        Spacer(modifier = modifier.weight(1f))
    }
}

@Composable
fun LayoutConfigButton (modifier: Modifier) {

    val context : Context = LocalContext.current

    IconButton (
        onClick = { context.startActivity(Intent(context, LayoutConfigActivity::class.java)) },
        modifier = modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Configure layout",
            tint = ViolinEsqueTheme.colors.textButton,
            modifier = modifier.size(30.dp)
        )
    }
}

@Composable
fun CalibrationSettingsButton (modifier: Modifier) {

    val context : Context = LocalContext.current

    IconButton (
        onClick = { context.startActivity(Intent(context, CalibrationSettingsActivity::class.java)) },
        modifier = modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Calibration",
            tint = ViolinEsqueTheme.colors.textButton,
            modifier = modifier.size(30.dp)
        )
    }
}

@Composable
fun RowScope.OpenStringButtonContainer (
    currentStringLiveData: LiveData<ViolinString>,
    onButtonTouched: () -> Unit,
    onButtonReleased: () -> Unit,
    enabled: Boolean,
    modifier: Modifier
) {

    BoxWithConstraints (modifier = modifier
        .fillMaxSize()
        .weight(1f)
    ) {
        if (enabled) {
            OpenStringButtonEnabled(
                currentStringLiveData,
                { onButtonTouched() },
                { onButtonReleased() },
                modifier
            )
        } else { OpenStringButtonDisabled() }
        StringsContainer(currentStringLiveData)
    }
}

@Composable
fun OpenStringButtonEnabled (
    currentStringLiveData: LiveData<ViolinString>,
    handleButtonTouched: () -> Unit,
    handleButtonReleased: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentString: ViolinString by currentStringLiveData.observeAsState(initial = ViolinString.A)
    var isTouched by remember { mutableStateOf(false) }

    Box(modifier = modifier
        .fillMaxSize()
        .background(color = if (isTouched) ViolinEsqueTheme.colors.fingerBoardTouched else ViolinEsqueTheme.colors.fingerBoard)
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {

                    awaitFirstDown()
                    isTouched = true
                    handleButtonTouched()

                    do {
                        val event = awaitPointerEvent()
                        event.changes.forEach { pointerInputChange: PointerInputChange ->
                            pointerInputChange.consume()
                        }
                    } while (event.changes.any { it.pressed })

                    isTouched = false
                    handleButtonReleased()
                }
            }
        }
    ) {
        Text(
            text = currentString.toString(),
            color = if (isTouched) ViolinEsqueTheme.colors.textButtonTouched else ViolinEsqueTheme.colors.textButton,
            modifier = modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun OpenStringButtonDisabled (modifier: Modifier = Modifier) {

    Box(modifier = modifier
        .fillMaxSize()
        .background(color = ViolinEsqueTheme.colors.fingerBoardTouched)
    )
}

@Composable
fun StringsContainer (currentStringLiveData: LiveData<ViolinString>, modifier: Modifier = Modifier) {

    val currentString : ViolinString by currentStringLiveData.observeAsState(initial = ViolinString.A)

    Row (modifier = modifier
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
//    configIterationLiveData: LiveData<Int>,
    getButtonInteractability: (Int) -> Interactability,
    currentStringLiveData: LiveData<ViolinString>,
    onButtonTouched: (Int) -> Unit,
    onButtonReleased: (Int) -> Unit,
    modifier: Modifier
) {

//    val configIteration = configIterationLiveData.observeAsState(0)
//    Log.w("PlayMode", "Config iteration $configIteration.")

    Column (modifier = modifier.weight(1f)) {
        for (i in 1..12) {
            if (getButtonInteractability(i) == Interactability.ENABLED) {
                Log.w("PlayMode", "Created button $i.")
                FingerPositionButtonEnabled(
                    i, currentStringLiveData,
                    { n -> onButtonTouched(n) },
                    { n -> onButtonReleased(n) },
                    modifier
                )
            } else if (getButtonInteractability(i) == Interactability.DISABLED) {
                FingerPositionButtonDisabled()
            } else { /* Hidden */ }
        }
    }
}

@Composable
fun ColumnScope.FingerPositionButtonEnabled (
    position: Int,
    currentStringLiveData: LiveData<ViolinString>,
    handleButtonTouched: (Int) -> Unit,
    handleButtonReleased: (Int) -> Unit,
    modifier: Modifier
) {
    val currentString: ViolinString by currentStringLiveData.observeAsState(initial = ViolinString.A)
    var isTouched by remember { mutableStateOf(false) }

    Box(modifier = modifier
        .weight(1f)
        .fillMaxSize()
        .background(color = if (isTouched) ViolinEsqueTheme.colors.buttonTouched else ViolinEsqueTheme.colors.button)
        .pointerInput(Unit) {

            forEachGesture {

                awaitPointerEventScope {

                    awaitFirstDown()
                    isTouched = true
                    handleButtonTouched(position)
                    Log.w("PlayMode", "Touched button $position.")

                    do {
                        val event = awaitPointerEvent()
                        event.changes.forEach { pointerInputChange: PointerInputChange ->
                            pointerInputChange.consume()
                        }
                    } while (event.changes.any { it.pressed })

                    isTouched = false
                    handleButtonReleased(position)
                }
            }
        }
    ) {
        val noteString = when (currentString) {
            ViolinString.G -> G_STRING_NOTES[position]
            ViolinString.D -> D_STRING_NOTES[position]
            ViolinString.A -> A_STRING_NOTES[position]
            ViolinString.E -> E_STRING_NOTES[position]
        }

        Text(
            text = noteString,
            color = if (isTouched) ViolinEsqueTheme.colors.textButtonTouched else ViolinEsqueTheme.colors.textButton,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun ColumnScope.FingerPositionButtonDisabled (modifier: Modifier = Modifier) {

    Box (modifier = modifier
        .fillMaxSize()
        .weight(1f)
        .background(color = ViolinEsqueTheme.colors.buttonTouched)
    )
}

@Preview
@Composable
fun PlayModePreview() {
    ViolinEsqueTheme {
        PlayMode(
            MutableLiveData(0),
            MutableLiveData(ViolinString.A),
            { n -> doNothing(n) },
            { n -> doNothing(n) },
            { n -> Interactability.ENABLED }
        )
    }
}

fun doNothing (n : Int) {

}