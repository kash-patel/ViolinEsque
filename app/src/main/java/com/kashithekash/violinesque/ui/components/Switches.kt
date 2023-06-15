package com.kashithekash.violinesque.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.kashithekash.violinesque.ui.theme.ViolinEsqueTheme

@Composable
fun SettingsRowSwitch(
    text: String,
    booleanStateLiveData: LiveData<Boolean>,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier
) {

    val state by booleanStateLiveData.observeAsState()

    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 5.dp)
            .clickable { onCheckedChange(!state!!) }
    ) {
        Box (
            contentAlignment = Alignment.CenterStart,
            modifier = modifier.weight(2f)
        ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Box (
            contentAlignment = Alignment.CenterEnd,
            modifier = modifier.weight(1f)
        ) {

            Switch(
                checked = state!!,
                onCheckedChange = { onCheckedChange(!state!!) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = Color.Transparent,
                    checkedBorderColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    uncheckedTrackColor = Color.Transparent,
                    uncheckedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                ),
                modifier = modifier.scale(0.8f)
            )
        }
    }
}