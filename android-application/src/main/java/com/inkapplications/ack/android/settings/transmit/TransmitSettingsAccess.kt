package com.inkapplications.ack.android.settings.transmit

import com.inkapplications.ack.android.settings.SettingsReadAccess
import com.inkapplications.ack.android.settings.SettingsWriteAccess
import com.inkapplications.ack.android.settings.observeData
import com.inkapplications.ack.android.settings.observeString
import com.inkapplications.ack.android.settings.setData
import com.inkapplications.ack.android.transmit.TransmitSettings
import com.inkapplications.ack.structures.symbolOf
import dagger.Reusable
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Provides read/write data access for transmission settings.
 */
@Reusable
class TransmitSettingsAccess @Inject constructor(
    readAccess: SettingsReadAccess,
    private val writeAccess: SettingsWriteAccess,
    private val transmitSettings: TransmitSettings,
) {
    /**
     * Current saved state of the device's repeat-transmission.
     */
    val repeatedMessageSettings = readAccess.observeString(transmitSettings.comment)
        .combine(readAccess.observeData(transmitSettings.symbol)) { message, symbol ->
            TransmitSettingsSavedData().apply {
                this.message = message
                this.symbol = symbol
            }
        }

    /**
     * Update the repeated transmission message's saved settings.
     */
    suspend fun updateRepeatedMessage(message: String, symbol: String) {
        writeAccess.setString(transmitSettings.comment, message)
        writeAccess.setData(transmitSettings.symbol, symbolOf(symbol[0], symbol[1]))
    }
}
