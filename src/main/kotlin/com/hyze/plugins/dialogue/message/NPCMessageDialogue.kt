package com.hyze.plugins.dialogue.message

import com.hyze.plugins.dialogue.DialogueType
import com.hyze.plugins.dialogue.Expression
import com.hyze.plugins.dialogue.Message
import com.rs.cache.loaders.NPCDefinitions
import com.rs.game.player.Player

class NPCMessageDialogue(expression: Expression, npcId: Int = 0, message: String) : Message(DialogueType.ENTITY, expression.id, npcId, -1, -1, "", arrayListOf(message), null) {

    override fun display(player: Player) {
        val builder = StringBuilder()
        for (line in message) {
            builder.append(" ").append(line)
        }
        val text: String = builder.toString()
        player.interfaceManager.sendChatBoxInterface(1184)
        player.packets.sendIComponentText(1184, 17, NPCDefinitions.getNPCDefinitions(npcId).getName())
        player.packets.sendIComponentText(1184, 13, text)
        player.packets.sendNPCOnIComponent(1184, 11, npcId)
        player.packets.sendIComponentAnimation(emoteId, 1184, 11)
    }
}