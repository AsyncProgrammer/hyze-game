package com.hyze.plugins.dialogue

import com.hyze.plugins.DialogueBuilder
import com.hyze.plugins.dialogue.message.OptionMessageDialogue
import com.rs.game.player.Player

class DialogueManager(val player: Player) {

    private var lastDialogue: DialogueBuilder? = null

    fun next(){
        lastDialogue?.callNextMessage()
    }

    fun start(dialogueBuilder: DialoguePlugin, npcId: Int) {
         if(lastDialogue != null){
             finish()
         }

        val plugin = dialogueBuilder.build(player, npcId)
        lastDialogue = plugin

        plugin.callMessage()
    }

    fun clickAction(option: OptionMessageDialogue.Option) {
        lastDialogue?.callNextMessage(option)
    }

    fun finish() {
        if(lastDialogue == null) return

        lastDialogue = null
        if (player.interfaceManager.containsChatBoxInter()) {
            player.interfaceManager.closeChatBoxInterface()
        }
    }

}