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

package com.hyze.plugins.dialogue.impl

import com.hyze.plugins.dialogue.DialoguePlugin
import com.hyze.plugins.dialogue.Expression
import com.rs.game.player.Player


/**
 * DESCRIPTION
 *
 * @author var_5
 * @date 05/09/2020 at 17:37
 */
class RemoveLater: DialoguePlugin() {

    override fun build(player: Player, npcId: Int) = player.createDialogue {
        npc("O que você quer?!", Expression.ANGRY)
    }

}