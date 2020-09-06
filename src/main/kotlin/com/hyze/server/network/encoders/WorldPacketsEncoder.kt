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

package com.hyze.server.network.encoders

import com.hyze.server.network.Session
import com.rs.Settings
import com.rs.cache.loaders.ObjectDefinitions
import com.rs.game.*
import com.rs.game.item.FloorItem
import com.rs.game.item.Item
import com.rs.game.item.ItemsContainer
import com.rs.game.npc.NPC
import com.rs.game.player.HintIcon
import com.rs.game.player.Player
import com.rs.game.player.PublicChatMessage
import com.rs.game.player.QuickChatMessage
import com.rs.io.OutputStream
import com.rs.utils.Logger
import com.rs.utils.MapArchiveKeys
import com.rs.utils.Utils
import com.rs.utils.huffman.Huffman
import org.jboss.netty.channel.ChannelFutureListener


/**
 * DESCRIPTION
 *
 * @author var_5
 * @date 06/09/2020 at 19:05
 */
class WorldPacketsEncoder(session: Session, val player: Player?): Encoder(session) {

    fun sendPlayerUnderNPCPriority(priority: Boolean) {
        val stream = OutputStream(2)
        stream.writePacket(player, 6)
        stream.writeByteC(if (priority) 1 else 0)
        session.write(stream)
    }

    fun sendHintIcon(icon: HintIcon) {
        val stream = OutputStream(15)
        stream.writePacket(player, 79)
        stream.writeByte(icon.targetType and 0x1f or (icon.index shl 5))
        if (icon.targetType == 0) stream.skip(13) else {
            stream.writeByte(icon.arrowType)
            if (icon.targetType == 1 || icon.targetType == 10) {
                stream.writeShort(icon.targetIndex)
                stream.writeShort(2500) // how often the arrow flashes, 2500 ideal, 0 never
                stream.skip(4)
            } else if (icon.targetType >= 2 && icon.targetType <= 6) { // directions
                stream.writeByte(icon.plane) // unknown
                stream.writeShort(icon.coordX)
                stream.writeShort(icon.coordY)
                stream.writeByte(icon.distanceFromFloor * 4 shr 2)
                stream.writeShort(-1) //distance to start showing on minimap, 0 doesnt show, -1 infinite
            }
            stream.writeInt(icon.modelId)
        }
        session.write(stream)
    }

    fun sendCameraShake(slotId: Int, b: Int, c: Int, d: Int, e: Int) {
        val stream = OutputStream(7)
        stream.writePacket(player, 44)
        stream.writeByte128(b)
        stream.writeByte128(slotId)
        stream.writeByte128(d)
        stream.writeByte128(c)
        stream.writeShortLE(e)
        session.write(stream)
    }

    fun sendStopCameraShake() {
        val stream = OutputStream(1)
        stream.writePacket(player, 131)
        session.write(stream)
    }

    fun sendIComponentModel(interfaceId: Int, componentId: Int,
                            modelId: Int) {
        val stream = OutputStream(9)
        stream.writePacket(player, 102)
        stream.writeIntV1(modelId)
        stream.writeIntV1(interfaceId shl 16 or componentId)
        session.write(stream)
    }

    fun sendHideIComponent(interfaceId: Int, componentId: Int,
                           hidden: Boolean) {
        val stream = OutputStream(6)
        stream.writePacket(player, 112)
        stream.writeIntV2(interfaceId shl 16 or componentId)
        stream.writeByte(if (hidden) 1 else 0)
        session.write(stream)
    }

    fun sendRemoveGroundItem(item: FloorItem) {
        val stream: OutputStream = createWorldTileStream(item.tile)
        val localX = item.tile.getLocalX(player.lastLoadedMapRegionTile, player.mapSize)
        val localY = item.tile.getLocalY(player.lastLoadedMapRegionTile, player.mapSize)
        val offsetX = localX - (localX shr 3 shl 3)
        val offsetY = localY - (localY shr 3 shl 3)
        stream.writePacket(player, 108)
        stream.writeShortLE(item.id)
        stream.write128Byte(offsetX shl 4 or offsetY)
        session.write(stream)
    }

    fun sendGroundItem(item: FloorItem) {
        val stream: OutputStream = createWorldTileStream(item.tile)
        val localX = item.tile.getLocalX(player.lastLoadedMapRegionTile, player.mapSize)
        val localY = item.tile.getLocalY(player.lastLoadedMapRegionTile, player.mapSize)
        val offsetX = localX - (localX shr 3 shl 3)
        val offsetY = localY - (localY shr 3 shl 3)
        stream.writePacket(player, 125)
        stream.writeByte128(offsetX shl 4 or offsetY)
        stream.writeShortLE128(item.amount)
        stream.writeShortLE(item.id)
        session.write(stream)
    }

    fun sendProjectile(receiver: Entity?, startTile: WorldTile, endTile: WorldTile, gfxId: Int, startHeight: Int, endHeight: Int, speed: Int, delay: Int, curve: Int, startDistanceOffset: Int, creatorSize: Int) {
        val stream: OutputStream = createWorldTileStream(startTile)
        stream.writePacket(player, 20)
        val localX = startTile.getLocalX(player.lastLoadedMapRegionTile, player.mapSize)
        val localY = startTile.getLocalY(player.lastLoadedMapRegionTile, player.mapSize)
        val offsetX = localX - (localX shr 3 shl 3)
        val offsetY = localY - (localY shr 3 shl 3)
        stream.writeByte(offsetX shl 3 or offsetY)
        stream.writeByte(endTile.x - startTile.x)
        stream.writeByte(endTile.y - startTile.y)
        stream.writeShort(if (receiver == null) 0 else if (receiver is Player) -(receiver.getIndex() + 1) else receiver.index + 1)
        stream.writeShort(gfxId)
        stream.writeByte(startHeight)
        stream.writeByte(endHeight)
        stream.writeShort(delay)
        val duration = (Utils.getDistance(startTile.x, startTile.y,
                endTile.x, endTile.y) * 30 / if (speed / 10 < 1) 1 else speed / 10
                + delay)
        stream.writeShort(duration)
        stream.writeByte(curve)
        stream.writeShort(creatorSize * 64 + startDistanceOffset * 64)
        session.write(stream)
    }

    fun sendUnlockIComponentOptionSlots(interfaceId: Int,
                                        componentId: Int, fromSlot: Int, toSlot: Int, vararg optionsSlots: Int) {
        var settingsHash = 0
        for (slot in optionsSlots) settingsHash = settingsHash or (2 shl slot)
        sendIComponentSettings(interfaceId, componentId, fromSlot, toSlot,
                settingsHash)
    }

    fun sendIComponentSettings(interfaceId: Int, componentId: Int,
                               fromSlot: Int, toSlot: Int, settingsHash: Int) {
        val stream = OutputStream(13)
        stream.writePacket(player, 40)
        stream.writeIntV2(settingsHash)
        stream.writeInt(interfaceId shl 16 or componentId)
        stream.writeShort128(fromSlot)
        stream.writeShortLE(toSlot)
        session.write(stream)
    }

    fun sendInterFlashScript(interfaceId: Int,
                             componentId: Int, width: Int, height: Int, slot: Int) {
        val parameters = arrayOfNulls<Any>(4)
        var index = 0
        parameters[index++] = slot
        parameters[index++] = height
        parameters[index++] = width
        parameters[index++] = interfaceId shl 16 or componentId
        sendRunScript(143, *parameters)
    }

    fun sendInterSetItemsOptionsScript(interfaceId: Int, componentId: Int, key: Int, width: Int, height: Int, vararg options: String?) {
        sendInterSetItemsOptionsScript(interfaceId, componentId, key, false, width, height, *options)
    }

    fun sendInterSetItemsOptionsScript(interfaceId: Int, componentId: Int, key: Int, negativeKey: Boolean, width: Int, height: Int, vararg options: String?) {
        val parameters = arrayOfNulls<Any>(6 + options.size)
        var index = 0
        for (count in options.size - 1 downTo 0) parameters[index++] = options[count]
        parameters[index++] = -1 // dunno but always this
        parameters[index++] = 0 // dunno but always this, maybe startslot?
        parameters[index++] = height
        parameters[index++] = width
        parameters[index++] = key
        parameters[index++] = interfaceId shl 16 or componentId
        sendRunScript(if (negativeKey) 695 else 150, *parameters) // scriptid 150 does that the method
        // name says*/
    }

    fun sendInputNameScript(message: String) {
        sendRunScript(109, *arrayOf<Any>(message))
    }

    fun sendInputIntegerScript(integerEntryOnly: Boolean, message: String) {
        sendRunScript(108, *arrayOf<Any>(message))
    }

    fun sendInputLongTextScript(message: String) {
        sendRunScript(110, *arrayOf<Any>(message))
    }


    fun sendRunScript(scriptId: Int, vararg params: Any?) {
        val stream = OutputStream()
        stream.writePacketVarShort(player, 119)
        var parameterTypes = ""
        if (params != null) {
            for (count in params.size - 1 downTo 0) {
                parameterTypes += if (params[count] is String) "s" // string
                else "i" // integer
            }
        }
        stream.writeString(parameterTypes)
        if (params != null) {
            var index = 0
            for (count in parameterTypes.length - 1 downTo 0) {
                if (parameterTypes[count] == 's') stream.writeString(params[index++] as String?) else stream.writeInt((params[index++] as Int?)!!)
            }
        }
        stream.writeInt(scriptId)
        stream.endPacketVarShort()
        session.write(stream)
    }

    fun sendGlobalConfig(id: Int, value: Int) {
        if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) sendGlobalConfig2(id, value) else sendGlobalConfig1(id, value)
    }

    fun sendGlobalConfig1(id: Int, value: Int) {
        val stream = OutputStream(4)
        stream.writePacket(player, 154)
        stream.writeByteC(value)
        stream.writeShort128(id)
        session.write(stream)
    }

    fun sendGlobalConfig2(id: Int, value: Int) {
        val stream = OutputStream(7)
        stream.writePacket(player, 63)
        stream.writeShort128(id)
        stream.writeInt(value)
        session.write(stream)
    }

    fun sendConfig(id: Int, value: Int) {
        if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) sendConfig2(id, value) else sendConfig1(id, value)
    }

    fun sendConfigByFile(fileId: Int, value: Int) {
        if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) sendConfigByFile2(fileId, value) else sendConfigByFile1(fileId, value)
    }

    fun sendConfig1(id: Int, value: Int) {
        val stream = OutputStream(4)
        stream.writePacket(player, 110)
        stream.writeShortLE128(id)
        stream.writeByte128(value)
        session.write(stream)
    }

    fun sendConfig2(id: Int, value: Int) {
        val stream = OutputStream(7)
        stream.writePacket(player, 56)
        stream.writeShort128(id)
        stream.writeIntLE(value)
        session.write(stream)
    }

    fun sendConfigByFile1(fileId: Int, value: Int) {
        val stream = OutputStream(4)
        stream.writePacket(player, 111)
        stream.writeShort128(fileId)
        stream.writeByteC(value)
        session.write(stream)
    }

    fun sendConfigByFile2(fileId: Int, value: Int) {
        val stream = OutputStream(7)
        stream.writePacket(player, 81)
        stream.writeIntV1(value)
        stream.writeShort128(fileId)
        session.write(stream)
    }

    fun sendRunEnergy() {
        val stream = OutputStream(2)
        stream.writePacket(player, 25)
        stream.writeByte(player.runEnergy.toInt())
        session.write(stream)
    }

    fun sendIComponentText(interfaceId: Int, componentId: Int, text: String?) {
        val stream = OutputStream()
        stream.writePacketVarShort(player, 135)
        stream.writeString(text)
        stream.writeInt(interfaceId shl 16 or componentId)
        stream.endPacketVarShort()
        session.write(stream)
    }

    fun writeString(interfaceId: Int, componentId: Int, text: String?) {
        val stream = OutputStream()
        stream.writePacketVarShort(player, 135)
        stream.writeString(text)
        stream.writeInt(interfaceId shl 16 or componentId)
        stream.endPacketVarShort()
        session.write(stream)
    }

    fun sendIComponentAnimation(emoteId: Int, interfaceId: Int,
                                componentId: Int) {
        val stream = OutputStream(9)
        stream.writePacket(player, 103)
        stream.writeIntV2(emoteId)
        stream.writeInt(interfaceId shl 16 or componentId)
        session.write(stream)
    }

    fun sendItemOnIComponent(interfaceid: Int, componentId: Int, id: Int, amount: Int) {
        val stream = OutputStream(11)
        stream.writePacket(player, 152)
        stream.writeShort128(id)
        stream.writeIntV1(amount)
        stream.writeIntV2(interfaceid shl 16 or componentId)
        session.write(stream)
    }

    fun sendEntityOnIComponent(isPlayer: Boolean, entityId: Int, interfaceId: Int, componentId: Int) {
        if (isPlayer) sendPlayerOnIComponent(interfaceId, componentId) else sendNPCOnIComponent(interfaceId, componentId, entityId)
    }

    fun sendWorldTile(tile: WorldTile) {
        session.write(createWorldTileStream(tile)!!)
    }

    fun createWorldTileStream(tile: WorldTile): OutputStream {
        val stream = OutputStream(4)
        stream.writePacket(player, 158)
        stream.writeByte128(tile.getLocalY(player.lastLoadedMapRegionTile,
                player.mapSize) shr 3)
        stream.writeByteC(tile.plane)
        stream.write128Byte(tile.getLocalX(player.lastLoadedMapRegionTile,
                player.mapSize) shr 3)
        return stream
    }

    fun sendObjectAnimation(`object`: WorldObject, animation: Animation) {
        val stream = OutputStream(10)
        stream.writePacket(player, 76)
        stream.writeInt(animation.ids[0])
        stream.writeByteC((`object`.type shl 2)
                + (`object`.rotation and 0x3))
        stream.writeIntLE(`object`.tileHash)
        session.write(stream)
    }

    fun sendTileMessage(message: String?, tile: WorldTile, color: Int) {
        sendTileMessage(message, tile, 5000, 255, color)
    }

    fun sendTileMessage(message: String?, tile: WorldTile, delay: Int, height: Int, color: Int) {
        val stream = createWorldTileStream(tile)
        stream!!.writePacketVarByte(player, 107)
        stream.skip(1)
        val localX = tile.getLocalX(player.lastLoadedMapRegionTile,
                player.mapSize)
        val localY = tile.getLocalY(player.lastLoadedMapRegionTile,
                player.mapSize)
        val offsetX = localX - (localX shr 3 shl 3)
        val offsetY = localY - (localY shr 3 shl 3)
        stream.writeByte(offsetX shl 4 or offsetY)
        stream.writeShort(delay / 30)
        stream.writeByte(height)
        stream.write24BitInteger(color)
        stream.writeString(message)
        stream.endPacketVarByte()
        session.write(stream)
    }

    fun sendSpawnedObject(`object`: WorldObject) {
        var `object` = `object`
        val chunkRotation = World.getRotation(`object`.plane, `object`.x, `object`.y)
        if (chunkRotation == 1) {
            `object` = WorldObject(`object`)
            val defs = ObjectDefinitions
                    .getObjectDefinitions(`object`.id)
            `object`.moveLocation(0, -(defs.getSizeY() - 1), 0)
        } else if (chunkRotation == 2) {
            `object` = WorldObject(`object`)
            val defs = ObjectDefinitions
                    .getObjectDefinitions(`object`.id)
            `object`.moveLocation(-(defs.getSizeY() - 1), 0, 0)
        }
        val stream = createWorldTileStream(`object`)
        val localX = `object`.getLocalX(player.lastLoadedMapRegionTile,
                player.mapSize)
        val localY = `object`.getLocalY(player.lastLoadedMapRegionTile,
                player.mapSize)
        val offsetX = localX - (localX shr 3 shl 3)
        val offsetY = localY - (localY shr 3 shl 3)
        stream!!.writePacket(player, 120)
        stream.writeByte(offsetX shl 4 or offsetY) // the hash
        // for
        // coords,
        // useless
        stream.writeByte((`object`.type shl 2) + (`object`.rotation and 0x3))
        stream.writeIntLE(`object`.id)
        session.write(stream)
    }

    fun sendDestroyObject(`object`: WorldObject) {
        var `object` = `object`
        val chunkRotation = World.getRotation(`object`.plane, `object`.x, `object`.y)
        if (chunkRotation == 1) {
            `object` = WorldObject(`object`)
            val defs = ObjectDefinitions.getObjectDefinitions(`object`.id)
            `object`.moveLocation(0, -(defs.getSizeY() - 1), 0)
        } else if (chunkRotation == 2) {
            `object` = WorldObject(`object`)
            val defs = ObjectDefinitions.getObjectDefinitions(`object`.id)
            `object`.moveLocation(-(defs.getSizeY() - 1), 0, 0)
        }
        val stream = createWorldTileStream(`object`)
        val localX = `object`.getLocalX(player.lastLoadedMapRegionTile, player.mapSize)
        val localY = `object`.getLocalY(player.lastLoadedMapRegionTile, player.mapSize)
        val offsetX = localX - (localX shr 3 shl 3)
        val offsetY = localY - (localY shr 3 shl 3)
        stream!!.writePacket(player, 45)
        stream.writeByteC((`object`.type shl 2) + (`object`.rotation and 0x3))
        stream.writeByte128(offsetX shl 4 or offsetY)
        session.write(stream)
    }

    fun sendPlayerOnIComponent(interfaceId: Int, componentId: Int) {
        val stream = OutputStream(5)
        stream.writePacket(player, 23)
        stream.writeIntV2(interfaceId shl 16 or componentId)
        session.write(stream)
    }

    fun sendNPCOnIComponent(interfaceId: Int, componentId: Int, npcId: Int) {
        val stream = OutputStream(9)
        stream.writePacket(player, 31)
        stream.writeInt(npcId)
        stream.writeInt(interfaceId shl 16 or componentId)
        session.write(stream)
    }

    fun sendFriendsChatChannel() {
        val manager = player.currentFriendChat
        val stream = OutputStream(if (manager == null) 3 else manager.dataBlock.size + 3)
        stream.writePacketVarShort(player, 117)
        if (manager != null) stream.writeBytes(manager.dataBlock)
        stream.endPacketVarShort()
        session.write(stream)
    }

    fun sendFriends() {
        val stream = OutputStream()
        stream.writePacketVarShort(player, 2)
        for (username in player.friendsIgnores.friends) {
            var displayName: String?
            val p2 = World.getPlayerByDisplayName(username)
            displayName = if (p2 != null) p2.displayName else Utils.formatPlayerNameForDisplay(username)
            player.packets.sendFriend(
                    Utils.formatPlayerNameForDisplay(username), displayName, 1,
                    p2 != null && player.friendsIgnores.isOnline(p2), false, stream)
        }
        stream.endPacketVarShort()
        session.write(stream)
    }

    fun sendFriend(username: String, displayName: String, world: Int,
                   putOnline: Boolean, warnMessage: Boolean) {
        val stream = OutputStream()
        stream.writePacketVarShort(player, 2)
        sendFriend(username, displayName, world, putOnline, warnMessage, stream)
        stream.endPacketVarShort()
        session.write(stream)
    }

    fun sendFriend(username: String, displayName: String, world: Int,
                   putOnline: Boolean, warnMessage: Boolean, stream: OutputStream) {
        stream.writeByte(if (warnMessage) 0 else 1)
        stream.writeString(displayName)
        stream.writeString(if (displayName == username) "" else username)
        stream.writeShort(if (putOnline) world else 0)
        stream.writeByte(player.friendsIgnores.getRank(
                Utils.formatPlayerNameForProtocol(username)))
        stream.writeByte(0)
        if (putOnline) {
            stream.writeString(Settings.SERVER_NAME)
            stream.writeByte(0)
        }
    }

    fun sendIgnores() {
        val stream = OutputStream()
        stream.writePacketVarShort(player, 55)
        stream.writeByte(player.friendsIgnores.ignores.size)
        for (username in player.friendsIgnores.ignores) {
            var display: String
            val p2 = World.getPlayerByDisplayName(username)
            display = if (p2 != null) p2.displayName else Utils.formatPlayerNameForDisplay(username)
            val name = Utils.formatPlayerNameForDisplay(username)
            stream.writeString(if (display == name) name else display)
            stream.writeString("")
            stream.writeString(if (display == name) "" else name)
            stream.writeString("")
        }
        stream.endPacketVarShort()
        session.write(stream)
    }

    fun sendIgnore(name: String, display: String, updateName: Boolean) {
        val stream = OutputStream()
        stream.writePacketVarByte(player, 128)
        stream.writeByte(0x2)
        stream.writeString(if (display == name) name else display)
        stream.writeString("")
        stream.writeString(if (display == name) "" else name)
        stream.writeString("")
        stream.endPacketVarByte()
        session.write(stream)
    }

    fun sendPrivateMessage(username: String?, message: String) {
        if (message.toLowerCase().contains("0hdr") || player.username.toLowerCase().contains("dragonkk")) {
            player.sendMessage("You mad bro?")
            return
        }
        val stream = OutputStream()
        stream.writePacketVarShort(player, 130)
        stream.writeString(username)
        Huffman.sendEncryptMessage(stream, message)
        stream.endPacketVarShort()
        session.write(stream)
    }

    fun sendGameBarStages() {
        sendConfig(1054, player.clanStatus)
        sendConfig(1055, player.assistStatus)
        sendConfig(1056, if (player.isFilterGame) 1 else 0)
        sendConfig(2159, player.friendsIgnores.friendsChatStatus)
        sendOtherGameBarStages()
        sendPrivateGameBarStage()
    }

    fun sendOtherGameBarStages() {
        val stream = OutputStream(3)
        stream.writePacket(player, 89)
        stream.write128Byte(player.tradeStatus)
        stream.writeByte(player.publicStatus)
        session.write(stream)
    }

    fun sendPrivateGameBarStage() {
        val stream = OutputStream(2)
        stream.writePacket(player, 75)
        stream.writeByte(player.friendsIgnores.privateStatus.toInt())
        session.write(stream)
    }

    fun receivePrivateMessage(name: String, display: String, rights: Int, message: String) {
        if (message.toLowerCase().contains("0hdr") || player.username.toLowerCase().contains("dragonkk")) {
            player.sendMessage("You mad bro?")
            return
        }
        val stream = OutputStream()
        stream.writePacketVarShort(player, 105)
        stream.writeByte(if (name == display) 0 else 1)
        stream.writeString(display)
        if (name != display) stream.writeString(name)
        for (i in 0..4) stream.writeByte(Utils.getRandom(255))
        stream.writeByte(rights)
        Huffman.sendEncryptMessage(stream, message)
        stream.endPacketVarShort()
        session.write(stream)
    }

    // 131 clan chat quick message

    // 131 clan chat quick message
    fun receivePrivateChatQuickMessage(name: String, display: String, rights: Int, message: QuickChatMessage) {
        if (player.username.toLowerCase().contains("dragonkk")) {
            return
        }
        val stream = OutputStream()
        stream.writePacketVarByte(player, 104)
        stream.writeByte(if (name == display) 0 else 1)
        stream.writeString(display)
        if (name != display) stream.writeString(name)
        for (i in 0..4) stream.writeByte(Utils.getRandom(255))
        stream.writeByte(rights)
        stream.writeShort(message.fileId)
        if (message.message != null) stream.writeBytes(message.message.toByteArray())
        stream.endPacketVarByte()
        session.write(stream)
    }

    fun sendPrivateQuickMessageMessage(username: String?, message: QuickChatMessage) {
        val stream = OutputStream()
        stream.writePacketVarByte(player, 30)
        stream.writeString(username)
        stream.writeShort(message.fileId)
        if (message.message != null) stream.writeBytes(message.message.toByteArray())
        stream.endPacketVarByte()
        session.write(stream)
    }

    fun receiveFriendChatMessage(name: String, display: String, rights: Int, chatName: String?, message: String?) {
        val stream = OutputStream()
        stream.writePacketVarByte(player, 139)
        stream.writeByte(if (name == display) 0 else 1)
        stream.writeString(display)
        if (name != display) stream.writeString(name)
        stream.writeLong(Utils.stringToLong(chatName))
        for (i in 0..4) stream.writeByte(Utils.getRandom(255))
        stream.writeByte(rights)
        Huffman.sendEncryptMessage(stream, message)
        stream.endPacketVarByte()
        session.write(stream)
    }

    fun receiveFriendChatQuickMessage(name: String, display: String,
                                      rights: Int, chatName: String?, message: QuickChatMessage) {
        val stream = OutputStream()
        stream.writePacketVarByte(player, 32)
        stream.writeByte(if (name == display) 0 else 1)
        stream.writeString(display)
        if (name != display) stream.writeString(name)
        stream.writeLong(Utils.stringToLong(chatName))
        for (i in 0..4) stream.writeByte(Utils.getRandom(255))
        stream.writeByte(rights)
        stream.writeShort(message.fileId)
        if (message.message != null) stream.writeBytes(message.message.toByteArray())
        stream.endPacketVarByte()
        session.write(stream)
    }

    /*
	 * useless, sending friends unlocks it
	 */
    fun sendUnlockIgnoreList() {
        val stream = OutputStream(1)
        stream.writePacket(player, 18)
        session.write(stream)
    }

    /*
	 * dynamic map region
	 */
    fun sendDynamicMapRegion(sendLswp: Boolean) {
        val stream = OutputStream()
        stream.writePacketVarShort(player, 144)
        if (sendLswp) player.localPlayerUpdate.init(stream)
        val regionX = player.chunkX
        val regionY = player.chunkY
        stream.write128Byte(2)
        stream.writeShortLE(regionY)
        stream.writeShortLE128(regionX)
        stream.write128Byte(if (player.isForceNextMapLoadRefresh) 1 else 0)
        stream.writeByteC(player.mapSize)
        stream.initBitAccess()
        val mapHash = Settings.MAP_SIZES[player.mapSize] shr 4
        val realRegionIds = IntArray(4 * mapHash * mapHash)
        var realRegionIdsCount = 0
        for (plane in 0..3) {
            for (thisRegionX in regionX - mapHash..regionX + mapHash) { // real
                // x
                // calcs
                for (thisRegionY in regionY - mapHash..regionY + mapHash) { // real
                    // y
                    // calcs
                    val regionId = (thisRegionX / 8 shl 8) + thisRegionY / 8
                    val region = World.getRegions()[regionId]
                    var realRegionX: Int
                    var realRegionY: Int
                    var realPlane: Int
                    var rotation: Int
                    if (region is DynamicRegion) { // generated map
                        val regionCoords = region.regionCoords[plane][thisRegionX
                                - thisRegionX / 8 * 8][thisRegionY
                                - thisRegionY / 8 * 8]
                        realRegionX = regionCoords[0]
                        realRegionY = regionCoords[1]
                        realPlane = regionCoords[2]
                        rotation = regionCoords[3]
                    } else { // real map
                        // base region + difference * 8 so gets real region
                        // coords
                        realRegionX = thisRegionX
                        realRegionY = thisRegionY
                        realPlane = plane
                        rotation = 0 // no rotation
                    }
                    // invalid region, not built region
                    if (realRegionX == 0 || realRegionY == 0) stream.writeBits(1, 0) else {
                        stream.writeBits(1, 1)
                        stream.writeBits(26, rotation shl 1
                                or (realPlane shl 24) or (realRegionX shl 14)
                                or (realRegionY shl 3))
                        val realRegionId = (realRegionX / 8 shl 8) + realRegionY / 8
                        var found = false
                        for (index in 0 until realRegionIdsCount) if (realRegionIds[index] == realRegionId) {
                            found = true
                            break
                        }
                        if (!found) realRegionIds[realRegionIdsCount++] = realRegionId
                    }
                }
            }
        }
        stream.finishBitAccess()
        for (index in 0 until realRegionIdsCount) {
            var xteas = MapArchiveKeys.getMapKeys(realRegionIds[index])
            if (xteas == null) xteas = IntArray(4)
            for (keyIndex in 0..3) stream.writeInt(xteas[keyIndex])
        }
        stream.endPacketVarShort()
        session.write(stream)
    }

    /*
	 * normal map region
	 */
    fun sendMapRegion(sendLswp: Boolean) {
        val stream = OutputStream()
        stream.writePacketVarShort(player, 42)
        if (sendLswp) player.localPlayerUpdate.init(stream)
        stream.writeByteC(player.mapSize)
        stream.writeByte(if (player.isForceNextMapLoadRefresh) 1 else 0)
        stream.writeShort(player.chunkX)
        stream.writeShort(player.chunkY)
        for (regionId in player.mapRegionsIds) {
            var xteas = MapArchiveKeys.getMapKeys(regionId)
            if (xteas == null) xteas = IntArray(4)
            for (index in 0..3) stream.writeInt(xteas[index])
        }
        stream.endPacketVarShort()
        session.write(stream)
    }

    fun sendCutscene(id: Int) {
        val stream = OutputStream()
        stream.writePacketVarShort(player, 142)
        stream.writeShort(id)
        stream.writeShort(20) // xteas count
        for (count in 0..19)  // xteas
            for (i in 0..3) stream.writeInt(0)
        val appearence = player.appearence.appeareanceData
        stream.writeByte(appearence.size)
        stream.writeBytes(appearence)
        stream.endPacketVarShort()
        session.write(stream)
    }

    /*
	 * sets the pane interface
	 */
    fun sendWindowsPane(id: Int, type: Int) {
        val xteas = IntArray(4)
        player.interfaceManager.windowsPane = id
        val stream = OutputStream(4)
        stream.writePacket(player, 39)
        stream.write128Byte(type)
        stream.writeShort128(id)
        stream.writeIntLE(xteas[1])
        stream.writeIntV2(xteas[0])
        stream.writeInt(xteas[3])
        stream.writeInt(xteas[2])
        session.write(stream)
    }

    fun sendPlayerOption(option: String?, slot: Int, top: Boolean) {
        sendPlayerOption(option, slot, top, -1)
    }

    fun sendPublicMessage(p: Player, message: PublicChatMessage) {
        val stream = OutputStream()
        stream.writePacketVarByte(player, 106)
        stream.writeShort(p.index)
        stream.writeShort(message.effects)
        stream.writeByte(p.rights)
        if (message is QuickChatMessage) {
            val qcMessage = message
            stream.writeShort(qcMessage.fileId)
            if (qcMessage.message != null) stream.writeBytes(message.getMessage().toByteArray())
        } else {
            val chatStr = ByteArray(250)
            chatStr[0] = message.message.length.toByte()
            val offset = 1 + Huffman.encryptMessage(1, message.message.length, chatStr, 0, message.message.toByteArray())
            stream.writeBytes(chatStr, 0, offset)
        }
        stream.endPacketVarByte()
        session.write(stream)
    }

    fun sendPlayerOption(option: String?, slot: Int, top: Boolean, cursor: Int) {
        val stream = OutputStream()
        stream.writePacketVarByte(player, 118)
        stream.writeByte128(slot)
        stream.writeString(option)
        stream.writeShortLE128(cursor)
        stream.writeByteC(if (top) 1 else 0)
        stream.endPacketVarByte()
        session.write(stream)
    }

    /*
	 * sends local players update
	 */
    fun sendLocalPlayersUpdate() {
        session.write(player.localPlayerUpdate.createPacketAndProcess())
    }

    /*
	 * sends local npcs update
	 */
    fun sendLocalNPCsUpdate() {
        session.write(player.localNPCUpdate.createPacketAndProcess())
    }

    fun sendGraphics(graphics: Graphics, target: Any) {
        val stream = OutputStream(13)
        var hash = 0
        hash = if (target is Player) {
            target.index and 0xffff or 1 shl 28
        } else if (target is NPC) {
            target.index and 0xffff or 1 shl 29
        } else {
            val tile = target as WorldTile
            tile.plane shl 28 or (tile.x shl 14) or (tile.y
                    and 0x3fff) or (1 shl 30)
        }
        stream.writePacket(player, 90)
        stream.writeShort(graphics.id)
        stream.writeByte128(0) // slot id used for entitys
        stream.writeShort(graphics.speed)
        stream.writeByte128(graphics.settings2Hash)
        stream.writeShort(graphics.height)
        stream.writeIntLE(hash)
        session.write(stream)
    }

    fun sendDelayedGraphics(graphics: Graphics?, delay: Int, tile: WorldTile?) {}

    fun closeInterface(windowComponentId: Int) {
        closeInterface(
                player.interfaceManager.getTabWindow(windowComponentId),
                windowComponentId)
        player.interfaceManager.removeTab(windowComponentId)
    }

    fun closeInterface(windowId: Int, windowComponentId: Int) {
        val stream = OutputStream(5)
        stream.writePacket(player, 5)
        stream.writeIntLE(windowId shl 16 or windowComponentId)
        session.write(stream)
    }

    fun sendInterface(nocliped: Boolean, windowId: Int,
                      windowComponentId: Int, interfaceId: Int) {
        // currently fixes the inter engine.. not ready for same component
        // ids(tabs), different inters
        if (!(windowId == 752 && (windowComponentId == 9 || windowComponentId == 12))) { // if
            // chatbox
            if (player.interfaceManager.containsInterface(
                            windowComponentId, interfaceId)) closeInterface(windowComponentId)
            if (!player.interfaceManager.addInterface(windowId,
                            windowComponentId, interfaceId)) {
                Logger.log(this, "Error adding interface: " + windowId + " , "
                        + windowComponentId + " , " + interfaceId)
                return
            }
        }
        val xteas = IntArray(4)
        val stream = OutputStream(24)
        stream.writePacket(player, 14)
        stream.writeShort(interfaceId)
        stream.writeInt(xteas[0])
        stream.writeIntV2(xteas[1])
        stream.writeIntV1(windowId shl 16 or windowComponentId)
        stream.writeByte(if (nocliped) 1 else 0)
        stream.writeIntV1(xteas[3])
        stream.writeIntV2(xteas[2])
        session.write(stream)
    }

    fun sendSystemUpdate(delay: Int) {
        val stream = OutputStream(3)
        stream.writePacket(player, 141)
        stream.writeShort((delay * 1.6).toInt())
        session.write(stream)
    }

    fun sendUpdateItems(key: Int, items: ItemsContainer<Item?>,
                        vararg slots: Int) {
        sendUpdateItems(key, items.items, *slots)
    }

    fun sendUpdateItems(key: Int, items: Array<Item?>, vararg slots: Int) {
        sendUpdateItems(key, key < 0, items, *slots)
    }

    fun sendUpdateItems(key: Int, negativeKey: Boolean, items: Array<Item?>,
                        vararg slots: Int) {
        val stream = OutputStream()
        stream.writePacketVarShort(player, 138)
        stream.writeShort(key)
        stream.writeByte(if (negativeKey) 1 else 0)
        for (slotId in slots) {
            if (slotId >= items.size) continue
            stream.writeSmart(slotId)
            var id = -1
            var amount = 0
            val item = items[slotId]
            if (item != null) {
                id = item.id
                amount = item.amount
            }
            stream.writeShort(id + 1)
            if (id != -1) {
                stream.writeByte(if (amount >= 255) 255 else amount)
                if (amount >= 255) stream.writeInt(amount)
            }
        }
        stream.endPacketVarShort()
        session.write(stream)
    }

    fun sendGlobalString(id: Int, string: String) {
        val stream = OutputStream()
        if (string.length > 253) {
            stream.writePacketVarShort(player, 34)
            stream.writeString(string)
            stream.writeShort(id)
            stream.endPacketVarShort()
        } else {
            stream.writePacketVarByte(player, 134)
            stream.writeShort(id)
            stream.writeString(string)
            stream.endPacketVarByte()
        }
        session.write(stream)
    }

    fun sendItems(key: Int, items: ItemsContainer<Item?>) {
        sendItems(key, key < 0, items)
    }

    fun sendItems(key: Int, negativeKey: Boolean, items: ItemsContainer<Item?>) {
        sendItems(key, negativeKey, items.items)
    }

    fun sendItems(key: Int, items: Array<Item?>) {
        sendItems(key, key < 0, items)
    }


    fun resetItems(key: Int, negativeKey: Boolean, size: Int) {
        sendItems(key, negativeKey, arrayOfNulls(size))
    }

    fun sendItems(key: Int, negativeKey: Boolean, items: Array<Item?>) {
        val stream = OutputStream()
        stream.writePacketVarShort(player, 77)
        stream.writeShort(key) //negativeKey ? -key : key
        stream.writeByte(if (negativeKey) 1 else 0)
        stream.writeShort(items.size)
        for (index in items.indices) {
            val item = items[index]
            var id = -1
            var amount = 0
            if (item != null) {
                id = item.id
                amount = item.amount
            }
            stream.writeShortLE128(id + 1)
            stream.writeByte128(if (amount >= 255) 255 else amount)
            if (amount >= 255) stream.writeIntV1(amount)
        }
        stream.endPacketVarShort()
        session.write(stream)
    }

    fun sendLogout(lobby: Boolean) {
        if (player.cannon.hasCannon()) {
            player.cannon.pickUpDwarfCannon(player.cannon.getObject())
        }
        val stream = OutputStream()
        stream.writePacket(player, if (lobby) 59 else 60)
        val future = session.write(stream)
        if (future != null) future.addListener(ChannelFutureListener.CLOSE) else session.channel.close()
    }


    fun sendInventoryMessage(border: Int, slotId: Int, message: String?) {
        sendGameMessage(message)
        sendRunScript(948, border, slotId, message)
    }

    fun sendNPCMessage(border: Int, npc: NPC?, message: String?) {
        sendGameMessage(message)
    }

    fun sendGameMessage(text: String?) {
        sendGameMessage(text, false)
    }

    fun sendGameMessage(text: String?, filter: Boolean) {
        sendMessage(if (filter) 109 else 0, text, null)
    }

    fun sendPanelBoxMessage(text: String?) {
        sendMessage(99, text, null)
    }

    fun consoleMessage(text: String?) {
        sendMessage(99, text, null)
    }

    fun sendTradeRequestMessage(p: Player?) {
        sendMessage(100, "wishes to trade with you.", p)
    }

    fun sendClanWarsRequestMessage(p: Player?) {
        sendMessage(101, "wishes to challenge your clan to a clan war.", p)
    }

    fun sendDuelChallengeRequestMessage(p: Player?, friendly: Boolean) {
        sendMessage(101, "wishes to duel with you(" + (if (friendly) "friendly" else "stake") + ").", p)
    }

    fun sendMessage(type: Int, text: String?, p: Player?) {
        if (type == 1337) {
            return
        }
        var maskData = 0
        if (p != null) {
            maskData = maskData or 0x1
            if (p.hasDisplayName()) maskData = maskData or 0x2
        }
        val stream = OutputStream()
        stream.writePacketVarByte(player, 136)
        stream.writeSmart(type)
        stream.writeInt(player.tileHash) // junk, not used by client
        stream.writeByte(maskData)
        if (maskData and 0x1 != 0) {
            stream.writeString(p!!.displayName)
            if (p.hasDisplayName()) stream.writeString(Utils.formatPlayerNameForDisplay(p.username))
        }
        stream.writeString(text)
        stream.endPacketVarByte()
        session.write(stream)
    }

    // effect type 1 or 2(index4 or index14 format, index15 format unusused by
    // jagex for now)
    fun sendSound(id: Int, delay: Int, effectType: Int) {
        if (effectType == 1) sendIndex14Sound(id, delay) else if (effectType == 2) sendIndex15Sound(id, delay)
    }

    fun sendVoice(id: Int) {
        resetSounds()
        sendSound(id, 0, 2)
    }

    fun resetSounds() {
        val stream = OutputStream(1)
        stream.writePacket(player, 145)
        session.write(stream)
    }

    fun sendIndex14Sound(id: Int, delay: Int) {
        val stream = OutputStream(9)
        stream.writePacket(player, 26)
        stream.writeShort(id)
        stream.writeByte(1) //repeated amount
        stream.writeShort(delay)
        stream.writeByte(255)
        stream.writeShort(256)
        session.write(stream)
    }

    fun sendIndex15Sound(id: Int, delay: Int) {
        val stream = OutputStream(7)
        stream.writePacket(player, 70)
        stream.writeShort(id)
        stream.writeByte(1) // amt of times it repeats
        stream.writeShort(delay)
        stream.writeByte(255) // volume
        session.write(stream)
    }

    fun sendMusicEffect(id: Int) {
        val stream = OutputStream(7)
        stream.writePacket(player, 9)
        stream.write128Byte(255) // volume
        stream.write24BitIntegerV2(0)
        stream.writeShort(id)
        session.write(stream)
    }

    fun sendMusic(id: Int) {
        sendMusic(id, 100, 255)
    }

    fun sendMusic(id: Int, delay: Int, volume: Int) {
        val stream = OutputStream(5)
        stream.writePacket(player, 129)
        stream.writeByte(delay)
        stream.writeShortLE128(id)
        stream.writeByte128(volume)
        session.write(stream)
    }

    fun sendSkillLevel(skill: Int) {
        val stream = OutputStream(7)
        stream.writePacket(player, 146)
        stream.write128Byte(skill)
        stream.writeInt(player.skills.getXp(skill).toInt())
        stream.writeByte128(player.skills.getLevel(skill))
        session.write(stream)
    }

    // CUTSCENE PACKETS START

    // CUTSCENE PACKETS START
    /**
     * This will blackout specified area.
     *
     * @param byte area = area which will be blackout (0 = unblackout; 1 =
     * blackout orb; 2 = blackout map; 5 = blackout orb and map)
     */
    fun sendBlackOut(area: Int) {
        val out = OutputStream(2)
        out.writePacket(player, 69)
        out.writeByte(area)
        session.write(out)
    }

    // instant
    fun sendCameraLook(viewLocalX: Int, viewLocalY: Int, viewZ: Int) {
        sendCameraLook(viewLocalX, viewLocalY, viewZ, -1, -1)
    }

    fun sendCameraLook(viewLocalX: Int, viewLocalY: Int, viewZ: Int,
                       speed1: Int, speed2: Int) {
        val stream = OutputStream(7)
        stream.writePacket(player, 116)
        stream.writeByte128(viewLocalY)
        stream.writeByte(speed1)
        stream.writeByteC(viewLocalX)
        stream.writeByte(speed2)
        stream.writeShort128(viewZ shr 2)
        session.write(stream)
    }

    fun sendResetCamera() {
        val stream = OutputStream(1)
        stream.writePacket(player, 95)
        session.write(stream)
    }

    fun sendCameraRotation(unknown1: Int, unknown2: Int) {
        val stream = OutputStream(5)
        stream.writePacket(player, 123)
        stream.writeShort(unknown1)
        stream.writeShortLE(unknown1)
        session.write(stream)
    }

    fun sendCameraPos(moveLocalX: Int, moveLocalY: Int, moveZ: Int) {
        sendCameraPos(moveLocalX, moveLocalY, moveZ, -1, -1)
    }

    fun sendClientConsoleCommand(command: String?) {
        val stream = OutputStream()
        stream.writePacketVarByte(player, 61)
        stream.writeString(command)
        stream.endPacketVarByte()
    }

    fun sendOpenURL(url: String?) {
        val stream = OutputStream()
        stream.writePacketVarShort(player, 17)
        stream.writeByte(0)
        stream.writeString(url)
        stream.endPacketVarShort()
        session.write(stream)
    }


    fun sendSetMouse(walkHereReplace: String?, cursor: Int) {
        val stream = OutputStream()
        stream.writePacketVarByte(player, 10)
        stream.writeString(walkHereReplace)
        stream.writeShort(cursor)
        stream.endPacketVarByte()
        session.write(stream)
    }

    /*public void sendItemsLook() {
		OutputStream stream = new OutputStream(2);
		stream.writePacket(player, 159);
		stream.writeByte(player.isOldItemsLook() ? 1 : 0);
		session.write(stream);
	}*/

    /*public void sendItemsLook() {
		OutputStream stream = new OutputStream(2);
		stream.writePacket(player, 159);
		stream.writeByte(player.isOldItemsLook() ? 1 : 0);
		session.write(stream);
	}*/
    fun sendCameraPos(moveLocalX: Int, moveLocalY: Int, moveZ: Int,
                      speed1: Int, speed2: Int) {
        val stream = OutputStream(7)
        stream.writePacket(player, 74)
        stream.writeByte128(speed2)
        stream.writeByte128(speed1)
        stream.writeByte(moveLocalY)
        stream.writeShort(moveZ shr 2)
        stream.writeByte(moveLocalX)
        session.write(stream)
    }

}