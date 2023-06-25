package com.inkapplications.ack.android.settings.transmit

import com.inkapplications.ack.android.R
import com.inkapplications.android.extensions.StringResources
import dagger.Reusable
import javax.inject.Inject

/**
 * Validates or generates errors for elements of the transmit settings form.
 */
@Reusable
class TransmitSettingsFormValidator @Inject constructor(
    private val stringResources: StringResources,
) {
    fun getMessageError(message: String): String? {
        return when {
            message.length > 43 -> stringResources.getString(R.string.settings_min_length_error, 43)
            else -> null
        }
    }

    fun getSymbolError(symbol: String): String? {
        return when {
            symbol.length != 2 -> stringResources.getString(R.string.settings_exact_length_error, 2)
            else -> null
        }
    }

    fun isValid(
        message: String,
        symbol: String,
    ): Boolean {
        return getMessageError(message) == null && getSymbolError(symbol) == null
    }
}
