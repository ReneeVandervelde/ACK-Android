package com.inkapplications.ack.android.settings

import com.inkapplications.ack.android.symbol.SymbolFactory
import dagger.Reusable
import javax.inject.Inject

/**
 * Creates view state objects based on settings data.
 */
@Reusable
class SettingsViewStateFactory @Inject constructor(
    private val symbolFactory: SymbolFactory,
) {
    /**
     * Create a view state object based on the user's current license data.
     */
    fun licenseState(data: LicenseData): LicenseViewState {
        return when {
            data.address == null -> LicenseViewState.Unconfigured
            data.passcode == null -> LicenseViewState.Registered(data.address.toString())
            else -> LicenseViewState.Verified(data.address.toString())
        }
    }

    /**
     * Create a list state from the current settings data.
     */
    fun viewState(data: List<SettingsGroup>): SettingsListViewState {
        return SettingsListViewState.Loaded(settingsList = data)
    }

    fun transmitIconState(data: TransmitSettingsData): TransmitSettingsButtonState {
        return when {
            data.address == null -> TransmitSettingsButtonState.Hidden
            else -> TransmitSettingsButtonState.Enabled(
                icon = symbolFactory.createSymbol(data.symbol),
                text = data.message,
            )
        }
    }
}
