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

package com.hyze

import com.hyze.server.Server
import com.hyze.utils.Logger
import com.rs.net.ServerChannelHandler


/**
 * Server starting
 *
 * @author Async
 * @date 17/07/2020 at 18:01
 */
object Engine {


    @JvmStatic
    fun main(vararg args: String) {
        val start = System.currentTimeMillis()
        Logger.warn("Starting server!")
        Logger.warn("Setuping dependencies injection")
        Server().start()

        Logger.warn("Starting server networking...")
        ServerChannelHandler.init()
        val end = System.currentTimeMillis()

        Logger.debug("Server took ${end - start}ms to start")
    }

}