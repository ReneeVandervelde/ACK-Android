package com.inkapplications.aprs.android.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.inkapplications.android.extensions.compose.ui.longClickable
import com.inkapplications.aprs.android.BuildConfig
import com.inkapplications.aprs.android.R
import com.inkapplications.aprs.android.input.IntPrompt
import com.inkapplications.aprs.android.input.StringPrompt
import com.inkapplications.aprs.android.ui.theme.AprsScreen
import com.inkapplications.aprs.android.ui.theme.AprsTheme
import com.inkapplications.aprs.android.ui.NavigationRow

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsScreen(
    state: State<SettingsViewModel?>,
    controller: SettingsController,
) = AprsScreen {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
        NavigationRow(
            title = stringResource(R.string.settings_title),
            onBackPressed = controller::onBackPressed,
        )
        val viewModel = state.value
        if (viewModel != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AprsTheme.spacing.gutter, vertical = AprsTheme.spacing.content),
            ) {
                val callsign = viewModel.callsignText
                if (callsign != null) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        elevation = 1.dp,
                        onClick = controller::onCallsignEditClick,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(AprsTheme.spacing.item),
                        ) {
                            if (viewModel.verified) Icon(Icons.Default.Verified, "Authenticated", modifier = Modifier.padding(end = AprsTheme.spacing.icon))
                            Text(callsign, style = AprsTheme.typography.h2)
                        }
                    }
                } else {
                    Button(onClick = controller::onCallsignEditClick) {
                        Text("Add Callsign")
                    }
                }
            }
            val promptSetting = remember { mutableStateOf<SettingState?>(null) }
            when (val settingState = promptSetting.value) {
                is SettingState.IntState -> IntPrompt(
                    title = settingState.setting.name,
                    value = settingState.value,
                    validator = settingState.setting.validator,
                    onDismiss = { promptSetting.value = null },
                    onSubmit = {
                        controller.onIntSettingChanged(settingState, it)
                        promptSetting.value = null
                    }
                )
                is SettingState.StringState -> StringPrompt(
                    title = settingState.setting.name,
                    value = settingState.value,
                    validator = settingState.setting.validator,
                    onDismiss = { promptSetting.value = null },
                    onSubmit = {
                        controller.onStringSettingChanged(settingState, it)
                        promptSetting.value = null
                    }
                )
                else -> {}
            }
            viewModel.settingsList.forEach { group ->
                Card(modifier = Modifier.padding(vertical = AprsTheme.spacing.item)) {
                    Column {
                        SettingsCategoryRow(group.name)
                        group.settings.forEach { item ->
                            when (item) {
                                is SettingState.BooleanState -> BooleanStateRow(item) {
                                    controller.onSwitchSettingChanged(item, it)
                                }
                                is SettingState.IntState -> IntStateRow(item) {
                                    promptSetting.value = item
                                }
                                is SettingState.StringState -> StringStateRow(item) {
                                    promptSetting.value = item
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            Text(
                text = stringResource(R.string.application_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE.toString()),
                modifier = Modifier
                    .longClickable(controller::onVersionLongPress)
                    .padding(AprsTheme.spacing.clickSafety)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}


@Composable
fun SettingsCategoryRow(name: String) = Row(
    Modifier.padding(horizontal = AprsTheme.spacing.gutter, vertical = AprsTheme.spacing.item)
) {
    Text(name, style = AprsTheme.typography.h2, modifier = Modifier.padding(vertical = AprsTheme.spacing.item))
}
