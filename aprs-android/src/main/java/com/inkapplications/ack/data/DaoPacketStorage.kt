package com.inkapplications.ack.data

import com.inkapplications.ack.codec.AprsCodec
import com.inkapplications.ack.structures.AprsPacket
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.station.Callsign
import com.inkapplications.coroutines.filterEachNotNull
import com.inkapplications.coroutines.mapEach
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

internal class DaoPacketStorage(
    private val packetDao: PacketDao,
    private val codec: AprsCodec,
    private val clock: Clock,
    private val logger: KimchiLogger,
): PacketStorage {
    override fun findRecent(count: Int): Flow<List<CapturedPacket>> {
        return packetDao.findRecent(count)
            .map { entities ->
                entities.mapNotNull { createCapturedPacket(it, fromEntityOrNull(it)) }
            }
            .filterEachNotNull()
    }

    override fun findById(id: Long): Flow<CapturedPacket?> {
        return packetDao.findById(id).map { it?.let { createCapturedPacket(it, fromEntityOrNull(it)) } }
    }

    override fun findByAddressee(callsign: Callsign): Flow<List<CapturedPacket>> {
        return packetDao.findByAddresseeCallsign(callsign.canonical)
            .mapEach { createCapturedPacket(it, fromEntityOrNull(it)) }
            .filterEachNotNull()
    }

    override suspend fun save(data: ByteArray, packet: AprsPacket, source: PacketSource): CapturedPacket {
        val entity = PacketEntity(
            id = null,
            timestamp = clock.now().toEpochMilliseconds(),
            data = data,
            packetSource = source,
            sourceCallsign = packet.route.source.callsign.canonical,
            addresseeCallsign = (packet.data as? PacketData.Message)?.addressee?.callsign?.canonical,
            dataType = packet.data.javaClass.simpleName,
        )
        val id = packetDao.addPacket(entity)

        return CapturedPacket(
            id = id,
            received = Instant.fromEpochMilliseconds(entity.timestamp),
            parsed = packet,
            source = entity.packetSource,
            raw = entity.data,
        )
    }

    private fun fromEntityOrNull(data: PacketEntity): AprsPacket? {
        try {
            return when (data.packetSource) {
                PacketSource.Ax25 -> codec.fromAx25(data.data)
                PacketSource.AprsIs -> codec.fromString(data.data.toString(Charsets.UTF_8))
            }
        } catch (error: Throwable) {
            logger.warn("Unable to parse packet", error)
            return null
        }
    }

    private fun createCapturedPacket(entity: PacketEntity, parsed: AprsPacket?, id: Long = entity.id!!): CapturedPacket? {
        parsed ?: return null

        return CapturedPacket(
            id = id,
            received = Instant.fromEpochMilliseconds(entity.timestamp),
            parsed = parsed,
            source = entity.packetSource,
            raw = entity.data,
        )
    }
}
