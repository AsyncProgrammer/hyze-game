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

package com.hyze.plugins.dialogue

import com.google.common.collect.Maps
import com.hyze.plugins.Plugin
import com.hyze.plugins.dialogue.message.NPCMessageDialogue
import com.hyze.plugins.dialogue.message.OptionMessageDialogue
import com.hyze.plugins.dialogue.message.PlainMessage
import com.hyze.plugins.dialogue.message.PlayerMessageDialogue
import com.rs.game.player.Player
import kotlinx.coroutines.coroutineScope

/**
 * Dialogue builder
 * @author var_5
 */

class DialogueBuilder(val player: Player) : Plugin<DialoguePlugin> {

    /**
     * Primary dialogue stage
     */

    var stage = 1

    /** Default options title*/
    private val DEFAULT_OPTION_TITLE = "Selecione uma opção"

    /** Default npc expression*/
    private val DEFAULT_EXPRESSION = Expression.HAPPY


    /**
     * The list of messages that will be sent
     * Key = Stage, Value = Message
     */

    private val messageMap: HashMap<Int, Message> = Maps.newHashMap()


    /**
     * The id of the entitiy we are interacting with
     */
    var npcId = -1

    /**
     * Send the dialogue message to next stage
     */

    private fun nextStage(){
        stage++
    }

    /**
     * If the dialogue has finished
     */

    private fun isOver(): Boolean{
        return stage > getLastStage()
    }

    /**
     * Ends the dialogue
     */
    fun end(){
        player.interfaceManager.closeChatBoxInterface()
        stage = -1
    }

    /**
     * Ends the dialogue with a unit function
     */

    fun end(function: () -> Unit){
        end()
        function.invoke()
    }

    /**
     * Get the dialogue last stage
     */
    private fun getLastStage() : Int{
        return messageMap.size
    }

    /**
     * Contruct a new dialogue message
     */

    private fun construct(message: Message){
        val size = messageMap.size
        messageMap[size + 1] = message
    }

    /**
     * Redirect a dialogue to X stage.
     * @param stage stage to go
     */

    fun redirect(stage: Int){
        this.stage = stage
        messageMap[stage]?.display(player)
    }

    /**
     * Create a npc message
     */

    fun npc(message: String, expression: Expression){
        construct(NPCMessageDialogue(expression, npcId, message))
    }

    /**
     * Creates a npc message with an default expression
     * @param message dialogue message
     */

    fun npc(message: String){
        construct(
            NPCMessageDialogue(
                Expression.HAPPY,
                npcId,
                message
            )
        )
    }

    /**
     * Options dialogue message
     * @param title options title
     * @param function unit
     */

    fun options(title: String, function: () -> Unit){
        construct(OptionMessageDialogue(title, arrayListOf()))
        function.invoke()
    }

    /**
     * Options with default title and a unit
     * @param function unit
     */

    fun options(function: () -> Unit){
        construct(
            OptionMessageDialogue(
                DEFAULT_OPTION_TITLE,
                arrayListOf()
            )
        )
        function.invoke()
    }

    /**
     * Option message with action
     * @param title option title
     * @param function the action executed by clicking
     */

   fun option(title: String, function: () -> Unit){
        messageMap.filterValues { it is OptionMessageDialogue }
                .forEach {
                    entry ->
                    run{
                        entry.value.message.add(title)
                        entry.value.actions?.add(function)
                    }
                }
    }

    fun plain(message: String){
        construct(PlainMessage(message))
    }

    /**
     * Display and call a message
     */

    fun callMessage(){
        val message = messageMap[stage]
        message?.display(player)

        if(message?.type == DialogueType.ENTITY){
            message.actions?.get(0)?.invoke()
       }
    }

    /**
     * Calling next dialogue message
     */

    fun callNextMessage(){
        nextStage()
        if(isOver()){
            player.newDialogueManager.finish()
            return
        }
        callMessage()
    }

    fun callNextMessage(option: OptionMessageDialogue.Option){
        val message = messageMap[stage] as OptionMessageDialogue
        when(option){
            OptionMessageDialogue.Option.ONE -> {
                message.actions?.get(0)?.invoke()
            }
            OptionMessageDialogue.Option.TWO -> {
                message.actions?.get(1)?.invoke()
            }
            OptionMessageDialogue.Option.THREE -> {
                message.actions?.get(2)?.invoke()
            }
            OptionMessageDialogue.Option.FOUR -> {
                message.actions?.get(3)?.invoke()
            }
            OptionMessageDialogue.Option.FIVE -> {
                message.actions?.get(4)?.invoke()
            }
        }
        callNextMessage()
    }

    /**
     * Constructing a player dialogue message
     * @param message message
     * @param expression player facial expression
     */

    fun message(message: String, expression: Expression){
        construct(PlayerMessageDialogue(message, expression))
    }

    infix fun Player.message(message: String){
        sendMessage(message)
    }

    /**
     * infixing send player message function
     * ${@usage (player chat = "")}
     */

    infix fun Player.chat(message: String){
        message(message, DEFAULT_EXPRESSION)
    }

}
