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

package com.hyze.server.network.encoders

import com.hyze.server.network.Session
import com.rs.Settings
import com.rs.cache.Cache
import com.rs.io.OutputStream
import org.jboss.netty.buffer.ChannelBuffer
import org.jboss.netty.buffer.ChannelBuffers
import org.jboss.netty.channel.ChannelFuture
import org.jboss.netty.channel.ChannelFutureListener

/**
 * DESCRIPTION
 *
 * @author var_5
 * @date 06/09/2020 at 18:42
 */
class GrabPacketsEncoder(session: Session): Encoder(session) {

    private lateinit var UKEYS_FILE: ByteArray
    var encryptionValue: Int = 0

    fun getUkeysFile(): OutputStream {
        return getContainerPacketData(255, 255, UKEYS_FILE)
    }

    fun sendOutdatedClientPacket() {
        val stream = OutputStream(1)
        stream.writeByte(6)
        val future: ChannelFuture? = session.write(stream)
        if (future != null) future.addListener(ChannelFutureListener.CLOSE) else session.channel.close()
    }

    fun sendStartUpPacket() {
        val stream = OutputStream(
                1 + Settings.GRAB_SERVER_KEYS.size * 4)
        stream.writeByte(0)
        for (key in Settings.GRAB_SERVER_KEYS) stream.writeInt(key)
        session.write(stream)
    }

    fun sendCacheArchive(indexId: Int, containerId: Int,
                         priority: Boolean) {
        if (indexId == 255 && containerId == 255) session.write(getUkeysFile()) else {
            session.write(getArchivePacketData(indexId, containerId, priority))
        }
    }

    fun getArchivePacketData(indexId: Int, archiveId: Int,
                             priority: Boolean): ChannelBuffer? {
        val archive = (if (indexId == 255) Cache.STORE.index255
                .getArchiveData(archiveId) else Cache.STORE.indexes[indexId]
                .mainFile.getArchiveData(archiveId))
                ?: return null
        val compression: Int = archive[0].toInt() and 0xff
        val length: Int = ((archive[1].toInt() and 0xff shl 24) + (archive[2].toInt() and 0xff shl 16)
                + (archive[3].toInt() and 0xff shl 8) + (archive[4].toInt() and 0xff))
        var settings = compression
        if (!priority) settings = settings or 0x80
        val buffer = ChannelBuffers.dynamicBuffer()
        buffer.writeByte(indexId)
        buffer.writeInt(archiveId)
        buffer.writeByte(settings)
        buffer.writeInt(length)
        val realLength = if (compression != 0) length + 4 else length
        for (index in 5 until realLength + 5) {
            if (buffer.writerIndex() % 512 == 0) {
                buffer.writeByte(255)
            }
            buffer.writeByte(archive[index].toInt())
        }
        val v: Int = encryptionValue
        if (v != 0) {
            for (i in 0 until buffer.arrayOffset()) buffer.setByte(i, buffer.getByte(i).toInt() xor v)
        }
        return buffer
    }

    fun getContainerPacketData(indexFileId: Int,
                               containerId: Int, archive: ByteArray): OutputStream {
        val stream = OutputStream(archive.size + 4)
        stream.writeByte(indexFileId)
        stream.writeInt(containerId)
        stream.writeByte(0)
        stream.writeInt(archive.size)
        var offset = 10
        for (index in archive.indices) {
            if (offset == 512) {
                stream.writeByte(255)
                offset = 1
            }
            stream.writeByte(archive[index].toInt())
            offset++
        }
        return stream
    }


}