package com.kashithekash.violinesque

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Paint.Align
import android.graphics.drawable.Icon
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kashithekash.violinesque.ui.theme.ViolinEsqueTheme

class CalibrationSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val calibrationSettingsActivityViewModel: CalibrationSettingsActivityViewModel = CalibrationSettingsActivityViewModel(this.application)

        calibrationSettingsActivityViewModel.getRotationVector().observe(this) {
            calibrationSettingsActivityViewModel.updateRoll(it[2])
        }

        setContent {
            ViolinEsqueTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ViolinEsqueTheme.colors.background
                ) {
                    CalibrationScreen(
                        calibrationSettingsActivityViewModel.getCurrentStringLiveData(),
                        { calibrationSettingsActivityViewModel.calibrateCCWLimit() },
                        { calibrationSettingsActivityViewModel.calibrateCWLimit() },
                        { calibrationSettingsActivityViewModel.resetCalibration() }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun CalibrationScreen (
    currentStringLiveData: LiveData<ViolinString> = MutableLiveData(ViolinString.A),
    setCCWLimit: () -> Unit = {},
    setCWLimit: () -> Unit = {},
    resetCalibration: () -> Unit = {},
    modifier: Modifier = Modifier
) {

    Column () {

        StringsContainer(
            currentStringLiveData = currentStringLiveData,
            modifier = modifier
                .fillMaxHeight()
                .weight(1f)
        )

        Column () {

            HorizontalLine(modifier = modifier)

            // Reset
            Box(contentAlignment = Alignment.Center,
                modifier = modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clickable { resetCalibration() }
            ) {
                Text(
                    text = "RESET ROLL POINTS",
                    textAlign = TextAlign.Center,
                    color = ViolinEsqueTheme.colors.text,
                )
            }

            HorizontalLine(modifier = modifier)

            // Calibration
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {

                Box(contentAlignment = Alignment.Center,
                    modifier = modifier
                        .fillMaxSize()
                        .weight(1f)
                        .clickable { setCCWLimit() }
                ) {
                    Text(
                        "SET G-D\nTRANSITION\nROLL POINT",
                        textAlign = TextAlign.Center,
                        color = ViolinEsqueTheme.colors.text,
                    )
                }

                VerticalLine(modifier = modifier)

                Box(contentAlignment = Alignment.Center,
                    modifier = modifier
                        .fillMaxSize()
                        .weight(1f)
                        .clickable { setCWLimit() }
                ) {
                    Text(
                        "SET A-E\nTRANSITION\nROLL POINT",
                        textAlign = TextAlign.Center,
                        color = ViolinEsqueTheme.colors.text,
                    )
                }
            }

            HorizontalLine(modifier = modifier)

            // Back
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                BackButton(modifier = modifier)
            }

            Spacer(modifier = modifier.height(10.dp))
        }
    }
}




