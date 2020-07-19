package com.hyze.plugins.dialogue.impl

import com.hyze.plugins.dialogue.DialogueBuilder
import com.hyze.plugins.dialogue.DialogueManifest
import com.hyze.plugins.dialogue.DialoguePlugin
import com.rs.game.player.Player

@DialogueManifest(npcNames = ["Hans", "Banker"])
class Banker : DialoguePlugin(){

    override fun build(player: Player, npcId: Int): DialogueBuilder = player.createDialogue(npcId) {
            npc("Bom dia, como posso lhe ajudar?")
            options("O que você gostaria de falar?") {
                option("Eu gostaria de acessar minha conta do banco, por favor"){
                    player.bank.openBank()
                }
                option("Eu gostaria de ver minha caixa de coleção"){
                    //TODO: Collection box
                }
                option("Gostaria de ver meus sets pré-montados"){
                    //TODO: Player pre-sets
                }
                option("O que é esse lugar?"){
                    npc("Isso é uma filial do Banco de Guilenor. Nós possuimos filiais em algumas cidades também.")
                    options("Título teste"){
                        option("E o que você faz?"){
                            npc("Eu cuido dos seus itens e dinheiro para você. Deixe seus objetos de valor conosco se quiser mantê-los seguros.")
                        }
                        option("Esse lugar não era chamado de Banco de Varrock?"){
                            npc("Sim, sim, mas as pessoas continuavam entrando em nossos galhos fora de Varrock e nos dizendo que nossos sinais estavam errados. Eles agiram como se não soubéssemos em que cidade estávamos ou algo assim.")
                        }
                    }
                }
            }

    }

}

