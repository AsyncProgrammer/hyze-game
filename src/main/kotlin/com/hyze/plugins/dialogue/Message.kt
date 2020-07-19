package com.hyze.plugins.dialogue

import com.rs.game.player.Player

abstract class Message(
        var type : DialogueType,
        var emoteId: Int,
        var npcId: Int,
        var itemId: Int,
        var amount: Int,
        var title: String,
        var message: ArrayList<String>,
        var actions: ArrayList<() -> Unit>?
) {


        abstract fun display(player: Player)

}