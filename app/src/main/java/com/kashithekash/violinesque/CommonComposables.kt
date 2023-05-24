package com.kashithekash.violinesque

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kashithekash.violinesque.ui.theme.ViolinEsqueTheme

@Composable
fun ConfigSideBar (modifier: Modifier = Modifier) {

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

    IconButton (
        onClick = { (context as Activity).finish() },
        modifier = modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = ViolinEsqueTheme.colors.textButton,
            modifier = modifier
                .size(30.dp)
        )
    }
}

@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(orientation) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            // restore original orientation when view disappears
            activity.requestedOrientation = originalOrientation
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}