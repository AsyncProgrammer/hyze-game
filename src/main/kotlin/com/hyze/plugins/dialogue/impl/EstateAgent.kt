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
import com.rs.game.player.Player
import com.rs.utils.ShopsHandler


/**
 * DESCRIPTION
 *
 * @author var_5
 * @date 19/07/2020 at 12:04
 */
class EstateAgent: DialoguePlugin() {

    override fun build(player: Player, npcId: Int) = player.createDialogue(npcId) {
        options {
            option("Eu gostaria de comprar algumas t�buas."){
                ShopsHandler.openShop(player, 38)
            }
            option("Linda capa que voc� tem! Isso � por que voc� � bom em constru��o?"){
                player message "O agente de estado n�o tem tempo dispon�vel para responder voc�!"
            }
            option("Umh, eu n�o tenho realmente nada para falar..."){
                end()
            }
        }
    }
}