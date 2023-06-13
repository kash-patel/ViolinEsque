package com.kashithekash.violinesque.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.kashithekash.violinesque.ui.theme.ViolinEsqueTheme

@Composable
fun SettingsRowButton(
    text: String,
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier
) {
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
    ) {
        Box (
            contentAlignment = Alignment.CenterStart,
            modifier = modifier.weight(4f)
        ) {
            Text(
                text = text,
                color = ViolinEsqueTheme.colors.text
            )
        }

        Spacer(modifier = modifier.weight(1f))

        IconButton(onClick = { onClick() }, modifier = modifier.weight(1f)) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = ViolinEsqueTheme.colors.text
            )
        }
    }
}