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

import com.hyze.plugins.dialogue.DialogueBuilder
import com.hyze.plugins.dialogue.DialoguePlugin
import com.rs.game.player.Player
import com.rs.utils.ShopsHandler


/**
 * DESCRIPTION
 *
 * @author var_5
 * @date 19/07/2020 at 11:47
 */

class Xuan: DialoguePlugin() {


    override fun build(player: Player, npcId: Int) = player.createDialogue(npcId) {
        npc("Ol� ${player.username}, eu me chamo Xuan. Eu estou aqui para vender auras por pontos de lealdade, eu estou apenas falando isso para voc� saber. Bem, o que voc� gostaria de perguntar?")
        options {
            option("Mostre-me sua loja"){
                ShopsHandler.openShop(player, 28)
            }
            option("Quantos pontos eu tenho?"){
                npc("Voc� atualmente possui ${player.loyaltyPoints} pontos de lealdade.")
            }
            option("Como eu obtenho pontos de lealdade?"){
                npc("A �nica maneira de obter pontos de lealdade � jogando por 30 minutos.")
            }
        }
    }
}