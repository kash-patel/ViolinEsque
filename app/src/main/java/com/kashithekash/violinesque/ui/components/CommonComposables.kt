package com.kashithekash.violinesque.ui.components

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.kashithekash.violinesque.ui.theme.ViolinEsqueTheme
import com.kashithekash.violinesque.utility.ViolinString

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
            .background(color = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
        )
    }
}

@Composable
fun HorizontalLine (lineWidth: Dp = 1.dp, lineColor: Color = MaterialTheme.colorScheme.outlineVariant, modifier: Modifier) {
    Box (
        modifier = modifier
            .fillMaxWidth()
            .height(lineWidth)
            .background(color = lineColor)
    )
}

@Composable
fun VerticalLine (lineWidth: Dp = 1.dp, lineColor: Color = MaterialTheme.colorScheme.outlineVariant, modifier: Modifier) {
    Box (
        modifier = modifier
            .fillMaxHeight()
            .width(lineWidth)
            .background(color = lineColor)
    )
}