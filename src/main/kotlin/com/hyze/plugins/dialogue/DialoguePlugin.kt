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

package com.hyze.plugins.dialogue

import com.hyze.plugins.DialogueBuilder
import com.rs.game.player.Player


/**
 * DESCRIPTION
 *
 * @author Async
 * @date 18/07/2020 at 22:31
 */
abstract class DialoguePlugin {

    abstract fun build(player: Player, npcId: Int) : DialogueBuilder

    inline fun Player.createDialogue(npcId: Int, init: DialogueBuilder.() -> Unit): DialogueBuilder{
        val dialoguePlugin = DialogueBuilder(this)
        dialoguePlugin.npcId = npcId
        dialoguePlugin.init()
        return dialoguePlugin
    }

}