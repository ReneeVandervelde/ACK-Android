package com.inkapplications.aprs.android.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.inkapplications.android.extensions.compose.ui.longClickable
import com.inkapplications.aprs.android.BuildConfig
import com.inkapplications.aprs.android.R
import com.inkapplications.aprs.android.ui.AprsScreen
import com.inkapplications.aprs.android.ui.AprsTheme
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
                    .padding(horizontal = AprsTheme.Spacing.gutter, vertical = AprsTheme.Spacing.content),
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
                            modifier = Modifier.padding(AprsTheme.Spacing.item),
                        ) {
                            if (viewModel.verified) Icon(Icons.Default.Verified, "Authenticated", modifier = Modifier.padding(end = AprsTheme.Spacing.icon))
                            Text(callsign, style = AprsTheme.Typography.h2)
                        }
                    }
                } else {
                    Button(onClick = controller::onCallsignEditClick) {
                        Text("Add Callsign")
                    }
                }
            }
            viewModel.settingsList.forEach { group ->
                Card(modifier = Modifier.padding(vertical = AprsTheme.Spacing.item)) {
                    Column {
                        SettingsCategoryRow(group.name)
                        group.settings.forEach { item ->
                            when (item) {
                                is SettingState.BooleanState -> BooleanStateRow(item) {
                                    controller.onSwitchSettingChanged(item, it)
                                }
                                is SettingState.IntState -> IntStateRow(item) {
                                    controller.onIntSettingClicked(item)
                                }
                                is SettingState.StringState -> StringStateRow(item) {
                                    controller.onStringSettingClicked(item)
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
                    .padding(AprsTheme.Spacing.clickSafety)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun SettingsCategoryRow(name: String) = Row(
    Modifier.padding(horizontal = AprsTheme.Spacing.gutter, vertical = AprsTheme.Spacing.item)
) {
    Text(name, style = AprsTheme.Typography.h2)
}
