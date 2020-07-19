package com.hyze.plugins

import com.google.common.collect.Maps
import com.hyze.plugins.dialogue.Expression
import com.rs.game.player.Player

object PluginRepository {

    private const val PLUGINS_PACKAGE = "com.hyze.plugins"
    private val pluginsRepository: HashMap<String, Plugin> = Maps.newHashMap()

    private fun registerPlugin(player: Player, key: String, plugin: Plugin){
        pluginsRepository[key] = plugin
    }

}