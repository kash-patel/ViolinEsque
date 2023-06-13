package com.kashithekash.violinesque.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kashithekash.violinesque.ui.theme.ViolinEsqueTheme

@Composable
fun NumberPickerHorizontal(
    value: Int,
    range: List<Int>,
    step: Int = 1,
    onValueChange: (Int) -> Unit,
    modifier: Modifier
) {

    Box (modifier = modifier.wrapContentSize()) {

        Row (verticalAlignment = Alignment.CenterVertically) {

            IconButton(
                enabled = value > range.first(),
                onClick = { onValueChange(value - step) },
                colors = IconButtonDefaults.iconButtonColors(
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowLeft,
                    contentDescription = "Decrement"
                )
            }

            Text(
                text = value.toString(),
                color = MaterialTheme.colorScheme.onSurface
            )

            IconButton(
                enabled = value < range.last(),
                onClick = { onValueChange(value + step) },
                colors = IconButtonDefaults.iconButtonColors(
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = "Increment"
                )
            }
        }
    }
}

@Composable
fun NumberPickerVertical(
    value: Int,
    min: Int,
    max: Int,
    step: Int = 1,
    onValueChange: (Int) -> Unit,
    modifier: Modifier
) {

    Box (modifier = modifier.wrapContentSize()) {

        Column (horizontalAlignment = Alignment.CenterHorizontally) {

            IconButton(
                enabled = value < max,
                onClick = {
                    onValueChange(value + step)
                },
                colors = IconButtonDefaults.iconButtonColors(
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Increment"
                )
            }

            Text(
                text = value.toString(),
                color = MaterialTheme.colorScheme.onSurface
            )

            IconButton(
                enabled = value > min,
                onClick = {
                    onValueChange(value - step)
                },
                colors = IconButtonDefaults.iconButtonColors(
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Decrement"
                )
            }
        }
    }
}