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

package com.hyze.server.network.packets

import com.rs.game.player.Player
import com.rs.io.InputStream


/**
 * DESCRIPTION
 *
 * @author Async
 * @date 19/07/2020 at 03:09
 */
interface IPacket<P> {

    fun <T> decode(stream: InputStream, player: Player): T

}