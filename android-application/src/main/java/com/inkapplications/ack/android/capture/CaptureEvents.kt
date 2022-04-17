package com.inkapplications.ack.android.capture

import android.Manifest
import com.inkapplications.android.extensions.location.LocationAccess
import com.inkapplications.ack.android.connection.ConnectionSettings
import com.inkapplications.ack.android.settings.SettingsReadAccess
import com.inkapplications.ack.android.settings.observeData
import com.inkapplications.ack.android.settings.observeInt
import com.inkapplications.ack.android.settings.observeString
import com.inkapplications.ack.android.transmit.TransmitSettings
import com.inkapplications.ack.data.drivers.PacketDriver
import com.inkapplications.ack.data.drivers.PacketDrivers
import com.inkapplications.ack.structures.*
import com.inkapplications.android.extensions.control.ControlState
import com.inkapplications.coroutines.combinePair
import com.inkapplications.coroutines.combineTriple
import inkapplications.spondee.scalar.WholePercentage
import inkapplications.spondee.structure.value
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext
import kotlin.math.roundToInt
import kotlin.time.ExperimentalTime

@Singleton
class CaptureEvents @Inject constructor(
    private val drivers: PacketDrivers,
    private val settings: SettingsReadAccess,
    private val connectionSettings: ConnectionSettings,
    private val transmitSettings: TransmitSettings,
    private val locationAccess: LocationAccess,
    private val logger: KimchiLogger,
) {
    private val audioListenState = MutableStateFlow(false)
    private val internetListenState = MutableStateFlow(false)
    private val audioTransmitState = MutableStateFlow(false)
    private val internetTransmitState = MutableStateFlow(false)

    private val audioCaptureControlState = audioListenState.map { listening ->
        if (listening) ControlState.On else ControlState.Off
    }

    private val combinedTransmitState = internetTransmitState.combinePair(audioTransmitState)

    private val internetCaptureControlState = settings.observeData(connectionSettings.address)
        .combine(internetListenState) { callsign, state ->
            when {
                callsign == null -> ControlState.Hidden
                state -> ControlState.On
                else -> ControlState.Off
            }
        }

    private val audioTransmitControlState = settings.observeData(connectionSettings.address)
        .combinePair(audioListenState)
        .combine(audioTransmitState) { (callsign, capturing), transmitting ->
            when {
                callsign == null -> ControlState.Hidden
                !capturing -> ControlState.Disabled
                transmitting -> ControlState.On
                else -> ControlState.Off
            }
        }

    private val internetTransmitControlState = settings.observeData(connectionSettings.address)
        .combinePair(settings.observeInt(connectionSettings.passcode))
        .combineTriple(internetListenState)
        .combine(internetTransmitState) { (callsign, passcode, capturing), transmitting ->
            when {
                callsign == null || passcode == -1 -> ControlState.Hidden
                !capturing -> ControlState.Disabled
                transmitting -> ControlState.On
                else -> ControlState.Off
            }
        }

    val audioCapturePermissions = drivers.afskDriver.receivePermissions
    val audioTransmitPermissions = drivers.afskDriver.transmitPermissions + Manifest.permission.ACCESS_FINE_LOCATION
    val internetCapturePermissions = drivers.afskDriver.receivePermissions
    val internetTransmitPermissions = drivers.afskDriver.transmitPermissions + Manifest.permission.ACCESS_FINE_LOCATION

    val screenState = audioCaptureControlState
        .combine(internetCaptureControlState) { recording, internet ->
            CaptureScreenViewModel(
                audioCaptureState = recording,
                internetCaptureState = internet,
            )
        }
        .combine(audioTransmitControlState) { viewModel, transmit ->
            viewModel.copy(audioTransmitState = transmit)
        }
        .combine(internetTransmitControlState) { viewModel, transmit ->
            viewModel.copy(internetTransmitState = transmit)
        }
        .combine(drivers.afskDriver.volume) { viewModel, volume ->
            viewModel.copy(audioLevel = "${volume?.value(WholePercentage)?.roundToInt()}%")
        }

    suspend fun connectAudio() {
        coroutineScope {
            launch { listenForPackets() }
            launch {
                audioTransmitState.collectLatest {
                    if (it) transmitLoop(drivers.afskDriver)
                }
            }
        }
    }

    suspend fun connectInternet() {
        coroutineScope {
            launch { listenForInternetPackets() }
            launch {
                internetTransmitState.collectLatest {
                    if (it) transmitLoop(drivers.internetDriver)
                }
            }
        }
    }

    suspend fun listenForPackets() {
        if (audioListenState.value) {
            logger.error("Tried to listen for audio packets while already active")
            return
        }
        try {
            audioListenState.value = true
            drivers.afskDriver.connect()
        } finally {
            logger.trace("Audio service cancelling")
            audioListenState.value = false
        }
    }

    fun startAudioTransmit() {
        audioTransmitState.value = true
    }
    fun stopAudioTransmit() {
        audioTransmitState.value = false
    }
    fun startInternetTransmit() {
        internetTransmitState.value = true
    }
    fun stopInternetTransmit() {
        internetTransmitState.value = false
    }

    @OptIn(ExperimentalTime::class)
    suspend fun transmitLoop(driver: PacketDriver) {
        settings.observeData(connectionSettings.address)
            .filterNotNull()
            .combine(settings.observeData(transmitSettings.digipath)) { callsign, path ->
                TransmitPrototype(
                    path = path,
                    destination = transmitSettings.destination.defaultData,
                    callsign = callsign,
                    symbol = transmitSettings.symbol.defaultData,
                    comment = transmitSettings.comment.defaultValue,
                    minRate = transmitSettings.minRate.defaultData,
                    maxRate = transmitSettings.maxRate.defaultData,
                    distance = transmitSettings.distance.defaultData,
                )
            }
            .combine(settings.observeData(transmitSettings.symbol)) { prototype, symbol ->
                prototype.copy(symbol = symbol)
            }
            .combine(settings.observeString(transmitSettings.comment)) { prototype, comment ->
                prototype.copy(comment = comment)
            }
            .combine(settings.observeData(transmitSettings.destination)) { prototype, destination ->
                prototype.copy(destination = destination)
            }
            .combine(settings.observeData(transmitSettings.minRate)) { prototype, rate ->
                prototype.copy(minRate = rate)
            }
            .combine(settings.observeData(transmitSettings.maxRate)) { prototype, rate ->
                prototype.copy(maxRate = rate)
            }
            .combine(settings.observeData(transmitSettings.distance)) { prototype, distance ->
                prototype.copy(distance = distance)
            }
            .flatMapLatest { prototype ->
                locationAccess.observeLocationChanges(prototype.maxRate, prototype.distance)
                    .map { prototype to it }
            }
            .combineTriple(combinedTransmitState)
            .collectLatest { (prototype, update) ->
                while (coroutineContext.isActive) {
                    val packet = AprsPacket(
                        route = PacketRoute(
                            source = prototype.callsign,
                            digipeaters = prototype.path,
                            destination = prototype.destination,
                        ),
                        data = PacketData.Position(
                            coordinates = update.location,
                            symbol = prototype.symbol,
                            altitude = update.altitude,
                            comment = prototype.comment,
                        )
                    )
                    val encodingConfig = EncodingConfig(compression = EncodingPreference.Disfavored)

                    driver.transmitPacket(packet, encodingConfig)
                    delay(prototype.minRate)
                }
            }
    }

    suspend fun listenForInternetPackets() {
        if (internetListenState.value) {
            logger.error("Tried to listen for internet packets while already active")
            return
        }
        try {
            internetListenState.value = true
            drivers.internetDriver.connect()
        } catch (e: Throwable) {
            logger.error("Internet listen terminated", e)
        } finally {
            logger.trace("Internet service cancelling")
            internetListenState.value = false
        }
    }
}
