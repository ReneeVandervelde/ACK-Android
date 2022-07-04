package com.inkapplications.ack.android.log.details

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.SettingsInputAntenna
import androidx.compose.material.icons.filled.Storage
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.log.SummaryFactory
import com.inkapplications.ack.android.locale.format
import com.inkapplications.ack.android.map.*
import com.inkapplications.ack.data.CapturedPacket
import com.inkapplications.ack.data.PacketSource
import com.inkapplications.ack.structures.PacketData.TelemetryReport
import com.inkapplications.ack.structures.PacketData.Weather
import com.inkapplications.ack.structures.capabilities.Commented
import com.inkapplications.ack.structures.capabilities.Mapable
import com.inkapplications.ack.structures.capabilities.Report
import com.inkapplications.android.extensions.StringResources
import com.inkapplications.android.extensions.ViewModelFactory
import com.inkapplications.android.extensions.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Convert Log data into the model used to render the view for a specific packet.
 */
class LogDetailsViewModelFactory @Inject constructor(
    private val markerFactory: ViewModelFactory<CapturedPacket, MarkerViewModel?>,
    private val summaryFactory: SummaryFactory,
    private val timeFormatter: DateTimeFormatter,
    private val stringResources: StringResources,
): ViewModelFactory<LogDetailData, LogDetailsState.LogDetailsViewModel> {
    override fun create(data: LogDetailData): LogDetailsState.LogDetailsViewModel {
        val packetData = data.packet.parsed.data
        return LogDetailsState.LogDetailsViewModel(
            callsign = data.packet.parsed.route.source.callsign,
            name = data.packet.parsed.route.source.toString(),
            timestamp = timeFormatter.formatTimestamp(data.packet.received)
                .let { stringResources.getString(R.string.capture_log_received_format, it) },
            comment = (packetData as? Commented)?.comment,
            markers = markerFactory.create(data.packet)?.let { listOf(it) }.orEmpty(),
            mapCameraPosition = (packetData as? Mapable)?.coordinates
                ?.let { MapCameraPosition(it, ZoomLevels.ROADS) }
                ?: CameraPositionDefaults.unknownLocation,
            temperature = (packetData as? Weather)?.temperature?.format(data.metric),
            wind = (packetData as? Weather)?.let { summaryFactory.createWindSummary(it.windData, data.metric) },
            altitude = (packetData as? Report)?.altitude?.format(data.metric),
            rawSource = data.packet.raw.decodeToString().takeIf { data.debug },
            telemetryValues = (packetData as? TelemetryReport)?.data,
            telemetrySequence = (packetData as? TelemetryReport)?.sequenceId,
            receiveIcon = when (data.packet.source) {
                PacketSource.AprsIs -> Icons.Default.Cloud
                PacketSource.Ax25 -> Icons.Default.SettingsInputAntenna
                PacketSource.Local -> Icons.Default.Storage
            },
            receiveIconDescription = when (data.packet.source) {
                PacketSource.Ax25 -> "Radio Packet"
                PacketSource.AprsIs -> "Internet Packet"
                PacketSource.Local -> "Local Packet"
            },
        )
    }
}

