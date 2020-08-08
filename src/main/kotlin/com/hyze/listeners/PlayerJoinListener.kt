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

package com.hyze.listeners

import com.hyze.events.Listener
import com.hyze.events.impl.PlayerJoinEvent


/**
 * DESCRIPTION
 *
 * @author var_5
 * @date 08/08/2020 at 14:26
 */
class PlayerJoinListener: Listener {

    fun handle(event: PlayerJoinEvent){
        event.joinMessage = "Bem vindo ao servidor ${event.player.username}"
    }

}