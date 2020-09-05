/*
 * RUNESCAPE PRIVATE SERVER FRAMEWORK
 * 
 * This file is part of the Hyze Server
 *
 * Hyze is a private RuneScape server focused primarily on
 * in the Brazilian community. The project has only 1 developer
 *
 * Objective of the project is to bring the best content, performance ever seen
 * by brazilians players in relation to private RuneScape servers (RSPS).
 */

package com.hyze.server.network.decoders

import com.rs.cache.Cache
import com.rs.io.InputStream
import com.rs.net.Session


/**
 * DESCRIPTION
 *
 * @author var_5
 * @date 05/09/2020 at 15:31
 */
class GrabPacketsDecoder(session: Session): Decoder(session) {

    override fun decode(session: Session, stream: InputStream) {
        while (stream.remaining > 0 && session.channel.isConnected) {
            val packetId = stream.readUnsignedByte()
            if (packetId == 0 || packetId == 1) decodeRequestCacheContainer(stream, packetId == 1) else decodeOtherPacket(stream, packetId)
        }
    }

    private fun decodeOtherPacket(stream: InputStream, packetId: Int) {
        if (packetId == 7) {
            session.channel.close()
            return
        }
        if (packetId == 4) {
            session.grabPackets.setEncryptionValue(stream.readUnsignedByte())
            if (stream.readUnsignedShort() != 0) session.channel.close()
        } else stream.skip(5)
    }

    private fun decodeRequestCacheContainer(stream: InputStream, priority: Boolean) {
        val indexId = stream.readUnsignedByte()
        val archiveId = stream.readInt()
        if (archiveId < 0) {
            return
        }
        if (indexId != 255) {
            if (Cache.STORE.indexes.size <= indexId || Cache.STORE.indexes[indexId] == null || !Cache.STORE.indexes[indexId].archiveExists(archiveId)) {
                return
            }
        } else if (archiveId != 255) if (Cache.STORE.indexes.size <= archiveId || Cache.STORE.indexes[archiveId] == null) {
            return
        }
        session.grabPackets.sendCacheArchive(indexId, archiveId, priority)
    }

}