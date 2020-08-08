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

package com.hyze.plugins.commands.impl

import com.hyze.plugins.commands.CommandPlugin
import com.hyze.plugins.commands.CommandManifest
import com.hyze.plugins.commands.PlayerRight
import com.rs.Settings
import com.rs.game.player.Player
import com.rs.game.player.content.Magic


/**
 * Home command
 *
 * @author var_5
 * @date 19/07/2020 at 18:33
 */

@CommandManifest(commands = ["home"], minRight = PlayerRight.NORMAL, consoleCommand = true)
class HomeCommand: CommandPlugin {

    override fun execute(player: Player, args: Array<out String>): Boolean {
        Magic.sendNormalTeleportSpell(player, 0, 0.0, Settings.RESPAWN_PLAYER_LOCATION, -1)
        return false
    }
}