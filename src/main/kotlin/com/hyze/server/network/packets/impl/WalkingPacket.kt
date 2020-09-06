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


/**
 * DESCRIPTION
 *
 * @author Async
 * @date 19/07/2020 at 10:59
 */
class WalkingPacket: IPacket {

    override fun decode(stream: InputStream, player: Player) {
        if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead)return
        val currentTime = Utils.currentTimeMillis()
        if (player.lockDelay > currentTime) return
        if (player.freezeDelay >= currentTime) {
            player.packets.sendGameMessage("A magical force prevents you from moving.")
            return
        }
        val length = stream.length
        val baseX = stream.readUnsignedShort128()
        val forceRun = stream.readUnsigned128Byte() == 1
        val baseY = stream.readUnsignedShort128()
        var steps = (length - 5) / 2
        if (steps > 25) steps = 25
        player.stopAll()

        if (forceRun) player.run = forceRun
        for (step in 0 until steps) if (!player.addWalkSteps(baseX + stream.readUnsignedByte(),
                        baseY + stream.readUnsignedByte(), 25, true)) break
    }
}