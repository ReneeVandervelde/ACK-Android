package com.inkapplications.ack.android.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.ui.theme.AckTheme

@Composable
fun IntStateRow(
    state: SettingState.IntState,
    onClick: () -> Unit
) = Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
        .clickable(onClick = onClick)
        .padding(vertical = AckTheme.spacing.clickSafety, horizontal = AckTheme.spacing.gutter)
) {
    Column {
        WarningLabel(state.setting)
        Text(state.setting.name, fontWeight = FontWeight.Bold)
    }
    Spacer(Modifier.weight(1f))
    Text(state.value.toString(), style = AckTheme.typography.caption)
}

@Composable
fun StringStateRow(
    state: SettingState.StringState,
    onClick: () -> Unit
) = Column(
    modifier = Modifier
        .clickable(onClick = onClick)
        .fillMaxWidth()
        .padding(vertical = AckTheme.spacing.clickSafety, horizontal = AckTheme.spacing.gutter)
) {
    WarningLabel(state.setting)
    Text(state.setting.name, fontWeight = FontWeight.Bold)
    Text(state.value, style = AckTheme.typography.caption)
}

@Composable
fun BooleanStateRow(
    state: SettingState.BooleanState,
    onChange: (Boolean) -> Unit,
) = Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
        .clickable { onChange(!state.value) }
        .padding(vertical = AckTheme.spacing.clickSafety, horizontal = AckTheme.spacing.gutter)
) {
    Column {
        WarningLabel(state.setting)
        Text(state.setting.name, fontWeight = FontWeight.Bold)
    }
    Spacer(Modifier.weight(1f))
    Switch(
        checked = state.value,
        onCheckedChange = { checked ->
            onChange(checked)
        }
    )
}

@Composable
private fun WarningLabel(setting: Setting) {
    when (setting.visibility) {
        SettingVisibility.Advanced -> Text(
            text = stringResource(R.string.settings_advanced_label),
            color = AckTheme.colors.warnForeground,
            style = AckTheme.typography.caption,
        )
        SettingVisibility.Dev -> Text(
            text = stringResource(R.string.settings_dev_label),
            color = AckTheme.colors.dangerForeground,
            style = AckTheme.typography.caption,
        )
        else -> {}
    }
}
