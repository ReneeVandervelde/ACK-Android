package com.inkapplications.ack.android.settings.transmit

import com.inkapplications.ack.structures.toTableCodePair
import dagger.Reusable
import javax.inject.Inject

/**
 * Converts data into view state for the Transmission Settings screen.
 */
@Reusable
class TransmitSettingsStateFactory @Inject constructor() {
    fun createFormState(data: TransmitSettingsSavedData): TransmitSettingsFormState {
        return TransmitSettingsFormState.Editable(
            message = data.message,
            symbol = data.symbol.toTableCodePair().let { "${it.first}${it.second}" },
        )
    }
}
