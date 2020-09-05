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

import com.hyze.utils.Settings
import com.rs.io.InputStream
import com.rs.net.Session


/**
 * DESCRIPTION
 *
 * @author var_5
 * @date 05/09/2020 at 14:52
 */
class ClientPacketsDecoder(session: Session) : Decoder(session) {

    override fun decode(session: Session, stream: InputStream) {
        session.setDecoder(-1)

        when(stream.readUnsignedByte()){
            14 -> decodeLogin(stream)
            15 -> decodeGrab(stream)
            else -> session.channel.close()
        }
    }

    private fun decodeGrab(stream: InputStream) {
        val size = stream.readUnsignedByte()
        if (stream.remaining < size) {
            session.channel.close()
            System.err.println("Incorrect size")
            return
        }
        session.setEncoder(0)
        val major = stream.readInt()
        val minor = stream.readInt()

        if (major != Settings.REVISION || minor != Settings.SUB_REVISION) {
            session.setDecoder(-1)
            session.grabPackets.sendOutdatedClientPacket()
            return
        }
        if (stream.readString() != Settings.GRAB_SERVER_TOKEN) {
            session.channel.close()
            return
        }
        session.setDecoder(1)
        session.grabPackets.sendStartUpPacket()
    }

    private fun decodeLogin(stream: InputStream) {
        if (stream.remaining != 0) {
            session.channel.close()
            return
        }
        session.setDecoder(2)
        session.setEncoder(1)
        session.loginPackets.sendStartUpPacket()
    }

}