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

package com.hyze.plugins.dialogue.message

import com.hyze.plugins.dialogue.DialogueType
import com.hyze.plugins.dialogue.Expression
import com.hyze.plugins.dialogue.Message
import com.rs.game.player.Player


/**
 * DESCRIPTION
 *
 * @author Async
 * @date 18/07/2020 at 12:21
 */
class PlayerMessageDialogue(message: String, expression: Expression) : Message(DialogueType.ENTITY, expression.id, -1, -1, -1, "", arrayListOf(message), null){

    override fun display(player: Player) {
        val builder = StringBuilder()
        for (line in message) {
            builder.append(" ").append(line)
        }
        val text: String = builder.toString()
        player.interfaceManager.sendChatBoxInterface(1191)
        player.packets.sendIComponentText(1191, 8, player.displayName)
        player.packets.sendIComponentText(1191, 17, text)
        player.packets.sendPlayerOnIComponent(1191, 15)
        player.packets.sendIComponentAnimation(emoteId, 1191, 15)
    }
}