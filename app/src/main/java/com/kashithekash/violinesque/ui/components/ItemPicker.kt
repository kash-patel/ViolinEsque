package com.kashithekash.violinesque.ui.components

import android.util.Log
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kashithekash.violinesque.ui.theme.ViolinEsqueTheme

@Composable
fun NumberPickerHorizontal(
    value: Int,
    range: List<Int>,
    onValueChange: (Int) -> Unit,
    modifier: Modifier
) {

    Box (modifier = modifier.wrapContentSize()) {

        Row (verticalAlignment = Alignment.CenterVertically) {

            IconButton(
                enabled = value > range.first(),
                onClick = { onValueChange(value - 1) },
                colors = IconButtonDefaults.iconButtonColors(
                    disabledContentColor = ViolinEsqueTheme.colors.textAlt,
                    contentColor = ViolinEsqueTheme.colors.text
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Remove,
                    contentDescription = "Decrement"
                )
            }

            Text(
                text = value.toString(),
                color = ViolinEsqueTheme.colors.text
            )

            IconButton(
                enabled = value < range.last(),
                onClick = { onValueChange(value + 1) },
                colors = IconButtonDefaults.iconButtonColors(
                    disabledContentColor = ViolinEsqueTheme.colors.textAlt,
                    contentColor = ViolinEsqueTheme.colors.text
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
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
    onValueChange: (Int) -> Unit,
    modifier: Modifier
) {

    Box (modifier = modifier.wrapContentSize()) {

        Column (horizontalAlignment = Alignment.CenterHorizontally) {

            IconButton(
                enabled = value < max,
                onClick = {
                    onValueChange(value + 1)
                },
                colors = IconButtonDefaults.iconButtonColors(
                    disabledContentColor = ViolinEsqueTheme.colors.textAlt,
                    contentColor = ViolinEsqueTheme.colors.text
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Increment"
                )
            }

            Text(
                text = value.toString(),
                color = ViolinEsqueTheme.colors.text
            )

            IconButton(
                enabled = value > min,
                onClick = {
                    onValueChange(value - 1)
                },
                colors = IconButtonDefaults.iconButtonColors(
                    disabledContentColor = ViolinEsqueTheme.colors.textAlt,
                    contentColor = ViolinEsqueTheme.colors.text
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