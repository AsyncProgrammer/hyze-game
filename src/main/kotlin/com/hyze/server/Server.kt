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

package com.hyze.server

import com.guardian.ItemManager
import com.rs.cache.Cache
import com.rs.cache.loaders.ItemsEquipIds
import com.rs.cores.CoresManager
import com.rs.game.RegionBuilder
import com.rs.game.World
import com.rs.game.npc.combat.CombatScriptsHandler
import com.rs.game.player.content.FishingSpotsHandler
import com.rs.game.player.content.FriendChatsManager
import com.rs.game.player.controlers.ControlerHandler
import com.rs.game.player.cutscenes.CutscenesHandler
import com.rs.game.player.dialogues.DialogueHandler
import com.rs.utils.*
import com.rs.utils.huffman.Huffman
import com.rs.utils.spawning.ObjectSpawning


/**
 * DESCRIPTION
 *
 * @author Async
 * @date 17/07/2020 at 18:04
 */
class Server {

    fun start(){
        println(" > Initializing server cache...")
        Cache.init()
        println(" > Loading server...")
        loadServer()
    }

    /**
     * Start npc usage
     */

    private fun loadServer(){
        ItemManager.inits()
        ItemsEquipIds.init()
        Huffman.init()
        DisplayNames.init()
        IPBanL.init()
        PkRank.init()
        DTRank.init()
        ObjectSpawns.init()
        NPCCombatDefinitionsL.init()
        NPCBonuses.init()
        NPCDrops.init()
        ItemExamines.init()
        ItemBonuses.init()
        MusicHints.init()
        ShopsHandler.init()
        FishingSpotsHandler.init()
        CombatScriptsHandler.init()
        DialogueHandler.init()
        ControlerHandler.init()
        CutscenesHandler.init()
        FriendChatsManager.init()
        CoresManager.init()
        World.init()
        RegionBuilder.init()
        NPCSpawns.loadSpawns()
        ObjectSpawning.loadSpawns()
    }

}