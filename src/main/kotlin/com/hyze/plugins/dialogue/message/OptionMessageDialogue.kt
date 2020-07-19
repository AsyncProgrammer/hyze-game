package com.hyze.plugins.dialogue.message

import com.hyze.plugins.dialogue.DialogueType
import com.hyze.plugins.dialogue.Message
import com.rs.game.player.Player

class OptionMessageDialogue(optionTitle: String, action: ArrayList<() -> Unit>?) : Message(
   DialogueType.OPTIONS,  -1, -1, -1, -1, optionTitle, arrayListOf(), action
) {

    private val OPTION_INTERFACE = 1188

    override fun display(player: Player) {
        player.interfaceManager.sendChatBoxInterface(OPTION_INTERFACE)
        player.packets.sendIComponentText(OPTION_INTERFACE, 20, title)
        for(line in 0..5){
            if(line < message.size){
                when(line){
                    0 -> player.packets.sendIComponentText(OPTION_INTERFACE, 3, message[line])
                    1 -> player.packets.sendIComponentText(OPTION_INTERFACE, 24, message[line])
                    2 -> player.packets.sendIComponentText(OPTION_INTERFACE, 29, message[line])
                    3 -> player.packets.sendIComponentText(OPTION_INTERFACE, 34, message[line])
                    4 -> player.packets.sendIComponentText(OPTION_INTERFACE, 39, message[line])
                }
            }else{
                when(line){
                    2 -> player.packets.sendHideIComponent(OPTION_INTERFACE, 14, true)
                    3 -> player.packets.sendHideIComponent(OPTION_INTERFACE, 15, true)
                    4 -> player.packets.sendHideIComponent(OPTION_INTERFACE, 16, true)
                }
            }
        }
    }

    enum class Option(val componentId: Int) {
        ONE(11), TWO(13), THREE(14), FOUR(15), FIVE(16);

        companion object{

            @JvmStatic
            fun getOption(componentId: Int) : Option? {
                for (option in values()) {
                    if (option.componentId == componentId) {
                        return option
                    }
                }
                return null
            }
        }

    }
}