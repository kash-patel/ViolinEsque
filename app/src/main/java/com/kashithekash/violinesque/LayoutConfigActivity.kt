package com.kashithekash.violinesque

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kashithekash.violinesque.ui.theme.ViolinEsqueTheme

class LayoutConfigActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val layoutConfigActivityViewModel: LayoutConfigActivityViewModel = LayoutConfigActivityViewModel(this.application)

        setContent {
            ViolinEsqueTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ViolinEsqueTheme.colors.background
                ) {
                    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                    LayoutConfigScreen(
                        { n -> layoutConfigActivityViewModel.getButtonInteractability(n) },
                        { n, i -> layoutConfigActivityViewModel.setButtonInteractability(n, i) }
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
}

@Composable
fun LayoutConfigScreen (
    getInteractability: (Int) -> Interactability,
    setInteractability: (Int, Interactability) -> Unit,
    modifier: Modifier = Modifier
) {
    Column (modifier = modifier
        .fillMaxSize()
    ) {
        Spacer(modifier = modifier.height(5.dp))
        Row (modifier = modifier
            .fillMaxSize()
            .weight(weight = 1f)
        ) {
            ConfigSideBar()
            Spacer(modifier = modifier.width(10.dp))
            BoxWithConstraints (modifier = modifier
                .fillMaxHeight()
                .weight(1f)) {
                OpenStringButton(
                    getInteractability(0),
                    { n, i -> setInteractability(n, i) },
                    modifier = modifier
                )
            }
            Column (
                modifier = modifier
                    .weight(1f)
            ) {
                for (i in 1..12) {
                    FingerPositionButton(
                        i,
                        getInteractability(i),
                        { n, i -> setInteractability(n, i) },
                        modifier = modifier
                    )
                }
            }
        }
    }
}

@Composable
fun OpenStringButton (
    initialInteractability: Interactability,
    onInteractabilityChange: (Int, Interactability) -> Unit,
    modifier: Modifier = Modifier
) {

    var interactability by remember { mutableStateOf(initialInteractability) }

    Box (modifier = modifier
        .fillMaxSize()
        .background(color = if (interactability == Interactability.ENABLED) ViolinEsqueTheme.colors.fingerBoardTouched else ViolinEsqueTheme.colors.fingerBoard)
        .clickable {
            interactability = when (interactability) {
                Interactability.ENABLED -> Interactability.DISABLED
                Interactability.DISABLED -> Interactability.ENABLED
                else -> Interactability.HIDDEN
            }

            onInteractabilityChange(0, interactability)
        }
    ) {
        Text(text = interactability.toString(),
            color = ViolinEsqueTheme.colors.textButton,
            modifier = modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun ColumnScope.FingerPositionButton (
    buttonNumber: Int,
    initialInteractability: Interactability,
    onInteractabilityChange: (Int, Interactability) -> Unit,
    modifier: Modifier
) {

    var interactability by remember { mutableStateOf(initialInteractability) }

    Box(modifier = modifier
        .weight(1f)
        .fillMaxSize()
        .clickable {
            interactability = toggleInteractability(interactability)
            onInteractabilityChange(buttonNumber, interactability)
        }
        .background(color = if (interactability == Interactability.ENABLED) ViolinEsqueTheme.colors.buttonTouched else ViolinEsqueTheme.colors.button)
    ) {
        Text(
            text = interactability.toString(),
            color = if (interactability == Interactability.HIDDEN) ViolinEsqueTheme.colors.textAlt else ViolinEsqueTheme.colors.textButton,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Preview
@Composable
fun LayoutConfigPreview() {
    ViolinEsqueTheme {
        LayoutConfigScreen(
            { n -> Interactability.ENABLED },
            { n, i -> Unit }
        )
    }
}

fun toggleInteractability (oldInteractability: Interactability) : Interactability {
    return when (oldInteractability) {
        Interactability.ENABLED -> Interactability.DISABLED
        Interactability.DISABLED -> Interactability.HIDDEN
        Interactability.HIDDEN -> Interactability.ENABLED
    }
}