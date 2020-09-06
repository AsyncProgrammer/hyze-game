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

import com.hyze.server.network.Session
import com.hyze.server.network.packets.PacketConstants
import com.hyze.utils.Logger
import com.hyze.utils.Settings
import com.rs.game.player.Player
import com.rs.io.InputStream
import com.rs.utils.Utils


/**
 * DESCRIPTION
 *
 * @author var_5
 * @date 05/09/2020 at 21:01
 */
class WorldPacketsDecoder(session: Session, val player: Player): Decoder(session) {

    val packetSizes = byteArrayOf(
            -1, -2, -1, 16, -1, 8, 8, 3, -1, 3, -1, -1,
            -1, 7, 8, 6, 2, 3, -1, -2, 3, 0, -1, 9, -1,
            9, 9, 8, 4, -1, 0, 3, 8, 4, 3, -1, -1, 17,
            4, 4, 9, -1, 3, 7, -2, 7, 3, 4, -1, 3, 11,
            3, -1, -1, 0, 8, 3, 7, -1, 9, -1, 7, 7, 12,
            4, 3, 11, 8, 8, 15, 1, 2, 6, -1, -1, -2, 16
            , 3, 1, 3, 9, 4, -2, 1, 1, 3, -1, 4, 3, -1,
            8, -2, -1, -1, 9, -2, 8, 2, 6, 2, -2, 3, 7, 4
    )

    override fun decode(session: Session, stream: InputStream) {
        while (stream.remaining > 0 && session.channel.isConnected && !player.hasFinished()){
            val packetId = stream.readPacket(player)

            if(packetId == 71) return

            if(packetId >= packetSizes.size || packetId < 0){
                if(Settings.DEBUG) Logger.warn("PacketID $packetId tem um id falso.")
                break
            }

            var packetLength = packetSizes[packetId]

            when(packetLength.toInt()){
                -1 -> stream.readUnsignedByte()
                -2 -> stream.readUnsignedShort()
                -3 -> stream.readInt()
                -4 -> {
                    packetLength = stream.remaining.toByte()
                    if(Settings.DEBUG){
                        println("Tamanho do packet $packetId altearada para $packetLength")
                    }
                }
            }

            if(packetLength > stream.remaining){
                packetLength = stream.remaining.toByte()
                if(Settings.DEBUG) println("$packetId packet possui um tamanho falso.")
            }

            val startOffset = stream.offset

            stream.offset = startOffset - packetLength
        }
    }

    fun processPackets(packetId: Int, stream: InputStream, length: Int){

        if(player.rights == 2){
            player.sendMessage("Packet processado $packetId - $length (Tamanho).")
        }

        player.packetsDecoderPing = Utils.currentTimeMillis()

        for (packetValues in PacketConstants.values()) {
            if(packetValues.sizes.contains(packetId)){
                val packet = packetValues.clazz.newInstance()
                packet.decode(stream, player)
            }
        }
    }
}