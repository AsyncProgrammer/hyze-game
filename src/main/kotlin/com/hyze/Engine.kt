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

import com.hyze.events.EventManager
import com.hyze.server.Server
import com.hyze.utils.Logger
import com.rs.game.player.LoyaltyManager
import com.rs.net.ServerChannelHandler
import org.koin.core.context.startKoin
import org.koin.dsl.module


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
        setupKoin()
        Server().start()
        Logger.warn("Starting server networking...")
        ServerChannelHandler.init()

        val end = System.currentTimeMillis()
        Logger.debug("Server took ${end - start}ms to start")
    }

    fun setupKoin(){
        startKoin {
            printLogger()
            modules(eventModules)
        }
    }

    val eventModules = module {
        single { EventManager() }
    }

}