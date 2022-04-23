package com.inkapplications.ack.data

import com.inkapplications.ack.structures.AprsPacket
import com.inkapplications.ack.structures.station.Callsign
import kotlinx.coroutines.flow.Flow

interface PacketStorage {
    fun findRecent(count: Int): Flow<List<CapturedPacket>>
    fun findLatestByConversation(callsign: Callsign): Flow<List<CapturedPacket>>
    fun findConversation(addressee: Callsign, callsign: Callsign): Flow<List<CapturedPacket>>
    fun findById(id: Long): Flow<CapturedPacket?>
    suspend fun save(data: ByteArray, packet: AprsPacket, source: PacketSource): CapturedPacket
}
