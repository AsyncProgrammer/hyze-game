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

package com.hyze.server.network.packets.impl

import com.hyze.server.network.packets.IPacket
import com.rs.game.player.Player
import com.rs.io.InputStream
import com.rs.utils.Utils
import com.rs.utils.huffman.Huffman


/**
 * DESCRIPTION
 *
 * @author Async
 * @date 19/07/2020 at 10:59
 */
class WalkingPacket: IPacket {

    override fun decode(stream: InputStream, player: Player): Boolean {
        if(player.lastPublicMessage > Utils.currentTimeMillis()) return true

        val colorEffect = stream.readUnsignedByte()
        val moveEffect = stream.readUnsignedByte()

        var message: String? = Huffman.decodeString(200, stream) ?: return true






        return false
    }
}