package com.inkapplications.ack.android.settings.transmit

/**
 * Actions that can be invoked from the Transmit Settings screen.
 */
interface TransmitSettingsController {
    /**
     * Invoked when the save button on the form is pressed.
     */
    fun onSaveForm(
        message: String,
        symbol: String
    )
}
