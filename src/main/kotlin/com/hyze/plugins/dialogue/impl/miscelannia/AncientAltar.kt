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
 * @date 19/07/2020 at 11:42
 */
class AncientAltar: DialoguePlugin() {

    override fun build(player: Player, npcId: Int) = player.createDialogue {
        options("Trocar livros de feitiços?") {
            option("Sim, troque meu livro de feitiços."){
                if(player.combatDefinitions.spellBook != 193) {
                    plain("Sua mente limpa e você troca, de volta para o livro anciente")
                    player.combatDefinitions.spellBook = 1
                }else{
                    plain("Sua mente limpa e você troca, de volta para o livro normal")
                    player.combatDefinitions.spellBook = 0
                }
            }
            option("Não."){
                end()
            }
        }
    }
}