package com.hyze.plugins.dialogue

import com.hyze.plugins.dialogue.message.OptionMessageDialogue
import com.rs.game.player.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class DialogueManager(val player: Player) {

    private var lastDialogue: DialogueBuilder? = null

    /**
     * Sends player to the next dialogue stage
     */

    fun next(){
        lastDialogue?.callNextMessage()
    }

    /**
     * Start a player dialogue
     */

    fun start(dialogueBuilder: DialoguePlugin, npcId: Int) {
            if (lastDialogue != null) {
                finish()
            }

            val plugin = dialogueBuilder.build(player, npcId)
            lastDialogue = plugin

            plugin.callMessage()
    }
    /**
     * Handle a dialogue click option
     */

    fun clickAction(option: OptionMessageDialogue.Option) {
        lastDialogue?.callNextMessage(option)
    }

    /**
     * Finish a dialogue
     */

    fun finish() {
        if(lastDialogue == null) return

        lastDialogue = null
        if (player.interfaceManager.containsChatBoxInter()) {
            player.interfaceManager.closeChatBoxInterface()
        }
    }


}