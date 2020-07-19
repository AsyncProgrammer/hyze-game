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

import com.rs.game.player.Player


/**
 * Dialogue plugin building
 *
 * @author Async
 * @date 18/07/2020 at 22:31
 */
abstract class DialoguePlugin {

    /**
     * Builds a new dialogue
     */

    abstract fun build(player: Player, npcId: Int) : DialogueBuilder

    inline fun Player.createDialogue(npcId: Int, init: DialogueBuilder.() -> Unit): DialogueBuilder {
        val dialoguePlugin = DialogueBuilder(this)
        dialoguePlugin.npcId = npcId
        dialoguePlugin.init()
        return dialoguePlugin
    }

    inline fun Player.createDialogue(init: DialogueBuilder.() -> Unit): DialogueBuilder {
        val dialoguePlugin = DialogueBuilder(this)
        dialoguePlugin.init()
        return dialoguePlugin
    }

}