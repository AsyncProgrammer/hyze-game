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

package com.hyze.plugins.dialogue.impl.events

import com.hyze.plugins.dialogue.DialogueBuilder
import com.hyze.plugins.dialogue.DialoguePlugin
import com.rs.game.WorldTile
import com.rs.game.player.Player


/**
 * DESCRIPTION
 *
 * @author var_5
 * @date 19/07/2020 at 11:32
 */
class Barrows: DialoguePlugin() {

    private val BARROWS_TILE = WorldTile(3534, 9677, 0)

    override fun build(player: Player, npcId: Int) = player.createDialogue {
            plain("Você encontrou um túnel escondido, você deseja entrar?")
            options {
                option("Sim, eu sou destemido"){
                    player.teleport(BARROWS_TILE)
                }
                option("Sem chances, isso aparenta ser muito assustador!"){
                    end()
                }
            }
    }
}