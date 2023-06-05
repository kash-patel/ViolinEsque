package com.kashithekash.violinesque.ui.components

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kashithekash.violinesque.ui.theme.ViolinEsqueTheme

@Composable
fun ConfigSideBar (modifier: Modifier) {

    Column (
        modifier = modifier
            .width(50.dp)
    ) {
        Spacer(modifier = modifier.weight(1f))
        BackButton(modifier = modifier)
        Spacer(modifier = modifier.height(30.dp))
    }
}

@Composable
fun BackButton (modifier: Modifier) {

    val context : Context = LocalContext.current

    Box (
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .clickable { (context as Activity).finish() }
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = ViolinEsqueTheme.colors.textButton,
            modifier = modifier.size(30.dp)
        )
    }
}

@Composable
fun HorizontalLine (lineWidth: Dp = 2.dp, lineColor: Color = ViolinEsqueTheme.colors.buttonTouched, modifier: Modifier) {
    Box (
        modifier = modifier
            .fillMaxWidth()
            .height(lineWidth)
            .background(color = lineColor)
    )
}

@Composable
fun VerticalLine (lineWidth: Dp = 2.dp, lineColor: Color = ViolinEsqueTheme.colors.buttonTouched, modifier: Modifier) {
    Box (
        modifier = modifier
            .fillMaxHeight()
            .width(lineWidth)
            .background(color = lineColor)
    )
}