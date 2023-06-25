package com.inkapplications.ack.android.settings

import android.graphics.Bitmap

sealed interface TransmitSettingsButtonState {
    object Hidden: TransmitSettingsButtonState
    data class Enabled(
        val icon: Bitmap?,
        val text: String,
    ): TransmitSettingsButtonState
}
