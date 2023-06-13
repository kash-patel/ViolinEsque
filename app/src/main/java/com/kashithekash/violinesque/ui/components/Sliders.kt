package com.kashithekash.violinesque.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlin.math.roundToInt

@Composable
fun IntegerSlider(
    text: String = "",
    initialValue: Int,
    min: Int,
    max: Int,
    step: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier
) {

    var sliderValue by remember { mutableFloatStateOf(initialValue.toFloat()) }

    Column (
        modifier = modifier.fillMaxWidth()
    ) {
        Box (
            contentAlignment = Alignment.CenterStart,
            modifier = modifier
                .fillMaxWidth()
        ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Box (
            contentAlignment = Alignment.Center,
            modifier = modifier
                .fillMaxWidth()
        ) {

            Slider(
                value = sliderValue,
                onValueChange = { newSliderValue -> sliderValue = newSliderValue },
                valueRange = min.toFloat()..max.toFloat(),
                steps = (max - min) / step - 1,
                onValueChangeFinished = { onValueChange(sliderValue.roundToInt()) },
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    activeTickColor = Color.Transparent,
                    inactiveTrackColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    inactiveTickColor = Color.Transparent
                )
            )
        }

        Box (
            contentAlignment = Alignment.Center,
            modifier = modifier
                .fillMaxWidth()
        ) {
            Text(
                text = sliderValue.roundToInt().toString(),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}