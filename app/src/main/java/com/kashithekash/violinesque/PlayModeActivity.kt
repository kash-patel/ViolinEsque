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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.kashithekash.violinesque.ui.theme.ViolinEsqueTheme
import kotlinx.coroutines.coroutineScope

private val G_STRING_NOTES: Array<String> =
    arrayOf("G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G")
private val D_STRING_NOTES: Array<String> =
    arrayOf("D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "C", "C#", "D")
private val A_STRING_NOTES: Array<String> =
    arrayOf("A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A")
private val E_STRING_NOTES: Array<String> =
    arrayOf("E", "F", "F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E")

val defaultInteractabilityArray: Array<Interactability> = arrayOf(
    Interactability.ENABLED,
    Interactability.ENABLED,
    Interactability.ENABLED,
    Interactability.ENABLED,
    Interactability.ENABLED,
    Interactability.ENABLED,
    Interactability.ENABLED,
    Interactability.ENABLED,
    Interactability.ENABLED,
    Interactability.ENABLED,
    Interactability.ENABLED,
    Interactability.ENABLED,
    Interactability.ENABLED
)

class PlayModeActivity : ComponentActivity() {

    private lateinit var playModeActivityViewModel: PlayModeActivityViewModel

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

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ViolinEsqueTheme.colors.background
                ) {
                    PlayMode(
                        playModeActivityViewModel.getConfigStateLiveData(),
                        playModeActivityViewModel.getCurrentStringLiveData(),
                        { n -> playModeActivityViewModel.buttonTouched(n) },
                        { n -> playModeActivityViewModel.buttonReleased(n) },
                        { s -> playModeActivityViewModel.stringChanged(s) },
                        { playModeActivityViewModel.getButtonInteractabilityArray() }
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
    configStateLiveData: LiveData<Long>,
    currentStringLiveData: LiveData<ViolinString>,
    onButtonTouched: (Int) -> Unit,
    onButtonReleased: (Int) -> Unit,
    onStringChanged: (ViolinString) -> Unit,
    getButtonInteractabilities: () -> Array<Interactability>,
    modifier: Modifier = Modifier
) {

    val configState by configStateLiveData.observeAsState(0f)
    val currentString by currentStringLiveData.observeAsState(ViolinString.A)

    var buttonInteractabilityArray: Array<Interactability> by remember { mutableStateOf(getButtonInteractabilities()) }

    LaunchedEffect(currentString) {
        onStringChanged(currentString)
    }

    LaunchedEffect(configState) {
        Log.w("PlayMode", "Config changed!")
        buttonInteractabilityArray = getButtonInteractabilities()
    }

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
            BoxWithConstraints (modifier = modifier
                .fillMaxHeight()
                .weight(1f)) {
                if (buttonInteractabilityArray[0] == Interactability.ENABLED) {
                    OpenStringButton_Enabled(
                        currentStringLiveData,
                        { s, n -> onButtonTouched(n) },
                        { s, n -> onButtonReleased(n) },
                        modifier
                    )
                } else { OpenStringButton_Disabled() }
                StringsContainer(currentStringLiveData)
            }
            Spacer(modifier = modifier.width(5.dp))
            Column (
                modifier = modifier
                    .weight(1f)
            ) {
                for (i in 1..12) {
                    if (buttonInteractabilityArray[i] == Interactability.ENABLED) {
                        FingerPositionButton_Enabled(
                            i, currentStringLiveData,
                            { s, n -> onButtonTouched(n) },
                            { s, n -> onButtonReleased(n) },
                            modifier
                        )
                    } else if (buttonInteractabilityArray[i] == Interactability.DISABLED) {
                        FingerPositionButton_Disabled()
                    } else { /* Hidden */ }
                }
            }
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
        InfoButton(modifier = modifier)
        Spacer(modifier = modifier.height(30.dp))
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
fun InfoButton (modifier: Modifier) {

    val context : Context = LocalContext.current

    IconButton (
        onClick = { context.startActivity(Intent(context, InfoActivity::class.java)) },
        modifier = modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Info",
            tint = ViolinEsqueTheme.colors.textButton,
            modifier = modifier
                .size(30.dp)
        )
    }
}

@Composable
fun OpenStringButton_Enabled (
    currentStringLiveData: LiveData<ViolinString>,
    handleButtonTouched: (ViolinString, Int) -> Unit,
    handleButtonReleased: (ViolinString, Int) -> Unit,
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
                    handleButtonTouched(currentString, 0)

                    do {
                        val event = awaitPointerEvent()
                        event.changes.forEach { pointerInputChange: PointerInputChange ->
                            pointerInputChange.consume()
                        }
                    } while (event.changes.any { it.pressed })

                    isTouched = false
                    handleButtonReleased(currentString, 0)
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
fun OpenStringButton_Disabled (modifier: Modifier = Modifier) {

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
        GString(currentString == ViolinString.G)
        DString(currentString == ViolinString.D)
        AString(currentString == ViolinString.A)
        EString(currentString == ViolinString.E)
    }
}

@Composable
fun RowScope.GString (isHighlighted: Boolean, modifier: Modifier = Modifier) {
    Box (
        modifier
            .fillMaxSize()
            .weight(1f)) {
        Box(modifier = modifier
            .align(Alignment.Center)
            .width(5.dp)
            .fillMaxHeight()
            .background(color = if (isHighlighted) ViolinEsqueTheme.colors.stringActive else ViolinEsqueTheme.colors.string)
        )
    }
}

@Composable
fun RowScope.DString (isHighlighted: Boolean, modifier: Modifier = Modifier) {
    Box (
        modifier
            .fillMaxSize()
            .weight(1f)) {
        Box(modifier = modifier
            .align(Alignment.Center)
            .width(4.dp)
            .fillMaxHeight()
            .background(color = if (isHighlighted) ViolinEsqueTheme.colors.stringActive else ViolinEsqueTheme.colors.string)
        )
    }
}

@Composable
fun RowScope.AString (isHighlighted: Boolean, modifier: Modifier = Modifier) {
    Box (
        modifier
            .fillMaxSize()
            .weight(1f)) {
        Box(modifier = modifier
            .align(Alignment.Center)
            .width(3.dp)
            .fillMaxHeight()
            .background(color = if (isHighlighted) ViolinEsqueTheme.colors.stringActive else ViolinEsqueTheme.colors.string)
        )
    }
}

@Composable
fun RowScope.EString (isHighlighted: Boolean, modifier: Modifier = Modifier) {
    Box (modifier = modifier
            .fillMaxSize()
            .weight(1f)) {
        Box(modifier = modifier
            .align(Alignment.Center)
            .width(2.dp)
            .fillMaxHeight()
            .background(color = if (isHighlighted) ViolinEsqueTheme.colors.stringActive else ViolinEsqueTheme.colors.string)
        )
    }
}

@Composable
fun ColumnScope.FingerPositionButton_Enabled (
    buttonNum: Int,
    currentStringLiveData: LiveData<ViolinString>,
    handleButtonTouched: (ViolinString, Int) -> Unit,
    handleButtonReleased: (ViolinString, Int) -> Unit,
    modifier: Modifier = Modifier
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
                    handleButtonTouched(currentString, buttonNum)

                    do {
                        val event = awaitPointerEvent()
                        event.changes.forEach { pointerInputChange: PointerInputChange ->
                            pointerInputChange.consume()
                        }
                    } while (event.changes.any { it.pressed })

                    isTouched = false
                    handleButtonReleased(currentString, buttonNum)
                }
            }
        }
    ) {
        val noteString = when (currentString) {
            ViolinString.G -> G_STRING_NOTES[buttonNum]
            ViolinString.D -> D_STRING_NOTES[buttonNum]
            ViolinString.A -> A_STRING_NOTES[buttonNum]
            ViolinString.E -> E_STRING_NOTES[buttonNum]
        }

        Text(
            text = noteString,
            color = if (isTouched) ViolinEsqueTheme.colors.textButtonTouched else ViolinEsqueTheme.colors.textButton,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun ColumnScope.FingerPositionButton_Disabled (modifier: Modifier = Modifier) {

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
            MutableLiveData<Long>(0),
            MutableLiveData(ViolinString.A),
            { n -> doNothing(n) },
            { n -> doNothing(n) },
            { s -> doNothing(0) },
            { defaultInteractabilityArray }
        )
    }
}

fun doNothing (n : Int) {

}