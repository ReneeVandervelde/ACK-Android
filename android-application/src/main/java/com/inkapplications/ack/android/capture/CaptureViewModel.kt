package com.inkapplications.ack.android.capture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkapplications.ack.android.connection.ConnectionSettings
import com.inkapplications.ack.android.settings.SettingsAccess
import com.inkapplications.ack.android.settings.SettingsReadAccess
import com.inkapplications.ack.android.settings.observeData
import com.inkapplications.ack.data.drivers.PacketDrivers
import com.inkapplications.coroutines.combine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Android ViewModel to create and store the state of the capture screen.
 */
@HiltViewModel
class CaptureViewModel @Inject constructor(
    captureEvents: CaptureEvents,
    settings: SettingsReadAccess,
    connectionSettings: ConnectionSettings,
    settingsAccess: SettingsAccess,
    drivers: PacketDrivers,
    captureScreenStateFactory: CaptureScreenStateFactory,
): ViewModel() {
    val controlPanelState: StateFlow<ControlPanelState> = combine(
        settings.observeData(connectionSettings.driver),
        settingsAccess.licenseData,
        captureEvents.audioInputVolume,
        captureEvents.connectionState,
        captureEvents.locationTransmitState,
        drivers.tncDriver.connectedDevice,
    ) { driver, license, audioInputVolume, connectedState, positionTransmitState, tncData ->
        captureScreenStateFactory.controlPanelState(
            currentDriver = driver,
            driverConnected = connectedState != ConnectionState.Disconnected,
            positionTransmit = positionTransmitState,
            license = license,
            inputAudioLevel = audioInputVolume,
            connectedTncDevice = tncData,
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ControlPanelState.Initial)
}
