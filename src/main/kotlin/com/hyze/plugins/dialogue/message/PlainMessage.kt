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
import com.hyze.plugins.dialogue.Message
import com.rs.game.player.Player


/**
 * DESCRIPTION
 *
 * @author var_5
 * @date 19/07/2020 at 11:33
 */
class PlainMessage(message: String): Message(DialogueType.NO_CONTINUATION, -1, -1, -1, -1, "", arrayListOf(message), null) {

    override fun display(player: Player) {
        val builder = StringBuilder()
        for (line in 0 until message.size) builder.append(if (line == 0) "<p=1>" else "<br>").append(message[line])
        val text = builder.toString()
        player.interfaceManager.sendChatBoxInterface(1186)
        player.packets.sendIComponentText(1186, 1, text)
    }

}