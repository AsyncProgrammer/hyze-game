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

package com.hyze.plugins.dialogue.impl.miscelannia

import com.hyze.plugins.dialogue.DialogueBuilder
import com.hyze.plugins.dialogue.DialoguePlugin
import com.rs.game.player.Player


/**
 * DESCRIPTION
 *
 * @author var_5
 * @date 19/07/2020 at 11:51
 */
class ZarosAltar : DialoguePlugin(){

    override fun build(player: Player, npcId: Int) = player.createDialogue {
        option("Trocar os cursos de ora��o?"){
            option("Sim, troque meu curso de ora��o"){
                if(!player.prayer.isAncientCurses){
                    plain("O altar enche sua cabe�a com pensamentos sombiros, limpando as ora��es da sua mem�ria e deixando apenas maldi��es em seu lugar.")
                    player.prayer.setPrayerBook(true)
                }else{
                    plain("O altar facilita o seu aperto no seu meio. As maldi��es escapam de sua mem�ria e voc� se lembra das ora��es que costumava conhecer")
                    player.prayer.setPrayerBook(false)
                }

            }
        }
    }
}