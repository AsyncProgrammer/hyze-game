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
import com.hyze.server.ServerProperties
import com.hyze.server.network.ServerChannelHandler
import com.hyze.utils.Logger
import com.hyze.utils.Settings


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
        ServerProperties.loadProperty()

        Logger.warn("Starting server networking...")
        println(Settings.PORT_ID)
        ServerChannelHandler()
        val end = System.currentTimeMillis()

        Logger.debug("Server took ${end - start}ms to start")
    }

}