package com.hyze.plugins.dialogue.impl

import com.hyze.plugins.dialogue.DialogueBuilder
import com.hyze.plugins.dialogue.DialogueManifest
import com.hyze.plugins.dialogue.DialoguePlugin
import com.rs.game.player.Player

@DialogueManifest(npcNames = ["Hans", "Banker"])
class Banker : DialoguePlugin(){

    override fun build(player: Player, npcId: Int): DialogueBuilder = player.createDialogue(npcId) {
            npc("Bom dia, como posso lhe ajudar?")
            options("O que voc� gostaria de falar?") {
                option("Eu gostaria de acessar minha conta do banco, por favor"){
                    player.bank.openBank()
                }
                option("Eu gostaria de ver minha caixa de cole��o"){
                    //TODO: Collection box
                }
                option("Gostaria de ver meus sets pr�-montados"){
                    //TODO: Player pre-sets
                }
                option("O que � esse lugar?"){
                    npc("Isso � uma filial do Banco de Guilenor. N�s possuimos filiais em algumas cidades tamb�m.")
                    options("T�tulo teste"){
                        option("E o que voc� faz?"){
                            npc("Eu cuido dos seus itens e dinheiro para voc�. Deixe seus objetos de valor conosco se quiser mant�-los seguros.")
                        }
                        option("Esse lugar n�o era chamado de Banco de Varrock?"){
                            npc("Sim, sim, mas as pessoas continuavam entrando em nossos galhos fora de Varrock e nos dizendo que nossos sinais estavam errados. Eles agiram como se n�o soub�ssemos em que cidade est�vamos ou algo assim.")
                        }
                    }
                }
            }

    }

}

