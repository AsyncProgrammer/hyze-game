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

import com.hyze.server.network.packets.impl.WalkingPacket
import kotlin.reflect.KClass


/**
 * DESCRIPTION
 *
 * @author var_5
 * @date 05/09/2020 at 13:14
 */
enum class PacketConstants(val clazz: Class<out IPacket>, val sizes: IntArray) {

    WALKING_PACKET(WalkingPacket::class.java, intArrayOf(5, 58))

}