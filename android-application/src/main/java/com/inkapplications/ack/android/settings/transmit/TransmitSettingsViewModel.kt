package com.inkapplications.ack.android.settings.transmit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Android Viewmodel to contain state for the Transmit Settings screen.
 */
@HiltViewModel
class TransmitSettingsViewModel @Inject constructor(
    private val transmitSettingsAccess: TransmitSettingsAccess,
    factory: TransmitSettingsStateFactory,
    private val validator: TransmitSettingsFormValidator,
): ViewModel() {
    private val pending = MutableStateFlow<TransmitSettingsFormState?>(null)
    val screenState = transmitSettingsAccess.repeatedMessageSettings
        .map { factory.createFormState(it) }
        .combine(pending) { saved, pending ->
            pending ?: saved
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, TransmitSettingsFormState.Inital)

    fun save(message: String, symbol: String) {
        pending.value = TransmitSettingsFormState.Draft(message, symbol)
        viewModelScope.launch {
            if (validator.isValid(message, symbol)) {
                transmitSettingsAccess.updateRepeatedMessage(message, symbol)
                pending.value = TransmitSettingsFormState.Finished
            } else {
                pending.value = TransmitSettingsFormState.Editable(
                    message = message,
                    symbol = symbol,
                    messageError = validator.getMessageError(message),
                    symbolError = validator.getSymbolError(symbol),
                )
            }
        }
    }
}
