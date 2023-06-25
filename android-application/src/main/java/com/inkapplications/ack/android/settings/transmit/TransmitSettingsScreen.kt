package com.inkapplications.ack.android.settings.transmit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.inkapplications.ack.android.ui.theme.AckScreen
import com.inkapplications.ack.android.ui.theme.AckTheme

@Composable
fun TransmitSettingsScreen(
    viewModel: TransmitSettingsViewModel = hiltViewModel(),
    controller: TransmitSettingsController,
) = AckScreen {
    val screenState = viewModel.screenState.collectAsState()
    Column(
        modifier = Modifier.padding(AckTheme.spacing.gutter),
    ) {
        Text(
            text = "Transmit Settings",
            style = AckTheme.typography.h1,
            modifier = Modifier.padding(bottom = AckTheme.spacing.item),
        )

        when (val formState = screenState.value) {
            TransmitSettingsFormState.Inital -> {}
            is TransmitSettingsFormState.FormState -> Form(formState, controller)
            TransmitSettingsFormState.Finished -> {}
        }
    }
}

@Composable
private fun Form(
    state: TransmitSettingsFormState.FormState,
    controller: TransmitSettingsController,
) = Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.fillMaxWidth(),
) {
    var message by rememberSaveable { mutableStateOf(state.message) }
    var symbol by rememberSaveable { mutableStateOf(state.symbol) }
    val enabled = state is TransmitSettingsFormState.Editable

    TextField(
        value = message,
        label = { Text("Message") },
        isError = state.messageError != null,
        onValueChange = { message = it },
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
    )
    state.messageError?.let { error ->
        Text(error, style = AckTheme.typography.errorCaption, modifier = Modifier.fillMaxWidth())
    }

    Spacer(modifier = Modifier.height(AckTheme.spacing.item))

    TextField(
        value = symbol,
        label = { Text("Symbol") },
        isError = state.symbolError != null,
        onValueChange = { symbol = it },
        enabled = enabled,
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
    state.symbolError?.let { error ->
        Text(error, style = AckTheme.typography.errorCaption, modifier = Modifier.fillMaxWidth())
    }

    Spacer(modifier = Modifier.height(AckTheme.spacing.item))

    Button(
        onClick = {
            controller.onSaveForm(message, symbol)
        },
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("Save")
    }
}

