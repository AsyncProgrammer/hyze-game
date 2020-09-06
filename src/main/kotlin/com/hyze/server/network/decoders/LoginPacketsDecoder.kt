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

package com.hyze.server.network.decoders

import com.hyze.server.network.Session
import com.hyze.utils.Logger
import com.hyze.utils.Settings
import com.rs.cache.Cache
import com.rs.game.World
import com.rs.game.player.Player
import com.rs.io.InputStream
import com.rs.net.sfs.CheckIP
import com.rs.utils.*
import org.xml.sax.SAXException
import java.io.IOException


/**
 * DESCRIPTION
 *
 * @author var_5
 * @date 05/09/2020 at 15:31
 */
class LoginPacketsDecoder(session: Session): Decoder(session) {

    @Transient
    var username: String? = null

    var LOCK = Object()

    override fun decode(session: Session, stream: InputStream) {
        synchronized(LOCK){
            session.setDecoder(-1)

            val packetId = stream.readUnsignedByte()

            if(World.exiting_start != 0L){
                session.loginPackets.sendClientPacket(14)
                return
            }

            val packetSize = stream.readUnsignedShort()

            if(packetSize != stream.remaining){
                session.channel.close()
                return
            }

            if(stream.readInt() != Settings.REVISION){
                session.loginPackets.sendClientPacket(6)
                return
            }

            if(packetId == 16 || packetId == 18){

            }else{
                if(Settings.DEBUG) {
                    Logger.warn("Id do Packet enviado $packetId")
                }
                session.channel.close()
            }
        }

    }

    private fun decodeWorldLogin(stream: InputStream) {
        synchronized(LOCK){
            if(stream.readInt() != Settings.SUB_REVISION){
                session.loginPackets.sendClientPacket(6)
                return
            }

            val unknownEquals14 = stream.readUnsignedByte() == 1
            val rsaBlockSize = stream.readUnsignedShort()

            if(rsaBlockSize > stream.remaining){
                session.loginPackets.sendClientPacket(10)
                return
            }

            val data = ByteArray(rsaBlockSize)
            stream.readBytes(data, 0, rsaBlockSize)
            val rsaStream = InputStream(Utils.cryptRSA(data, Settings.PRIVATE_EXPONENT, Settings.MODULUS))

            if(rsaStream.readUnsignedByte() != 10){
                session.loginPackets.sendClientPacket(10)
                return
            }

            val isaacKeys = IntArray(4)
            for (i in isaacKeys.indices) isaacKeys[i] = rsaStream.readInt()

            if(rsaStream.readLong() != 0L){
                session.loginPackets.sendClientPacket(10)
                return
            }

            var password = rsaStream.readString()

            if (password.length > 30 || password.length < 3) {
                session.loginPackets.sendClientPacket(3)
                return
            }

            val realPass = password
            password = Encrypt.encryptSHA1(password)
            val unknown = Utils.longToString(rsaStream.readLong())

            rsaStream.readLong() // random value
            rsaStream.readLong() // random value

            stream.decodeXTEA(isaacKeys, stream.offset, stream.length)
            val stringUsername = stream.readUnsignedByte() == 1 // unknown

            val username =
                Utils.formatPlayerNameForProtocol(if (stringUsername) stream.readString() else Utils.longToString(stream.readLong()))

            val displayMode = stream.readUnsignedByte()
            val screenWidth = stream.readUnsignedShort()
            val screenHeight = stream.readUnsignedShort()
            val unknown2 = stream.readUnsignedByte()
            stream.skip(24) // 24bytes directly from a file, no idea whats there

            val settings = stream.readString()
            val affid = stream.readInt()
            stream.skip(stream.readUnsignedByte())
            val mInformation: MachineInformation? = null
            val unknown3 = stream.readInt()
            val userFlow = stream.readLong()
            val hasAditionalInformation = stream.readUnsignedByte() == 1
            if (hasAditionalInformation) stream.readString() // aditionalInformation

            val hasJagtheora = stream.readUnsignedByte() == 1
            val js = stream.readUnsignedByte() == 1
            val hc = stream.readUnsignedByte() == 1
            val unknown4 = stream.readByte()
            val unknown5 = stream.readInt()
            val unknown6 = stream.readString()
            val unknown7 = stream.readUnsignedByte() == 1

            for (index in Cache.STORE.indexes.indices) {
                val crc = if (Cache.STORE.indexes[index] == null) -1011863738 else Cache.STORE.indexes[index].crc
                val receivedCRC = stream.readInt()
                if (crc != receivedCRC && index < 32) {
                    session.loginPackets.sendClientPacket(6)
                    return
                }
            }

            if (com.rs.Settings.enableSfs) {
                try {
                    if (!CheckIP.isGoodIp(session.channelIp)) {
                        session.loginPackets.sendClientPacket(20)
                        return
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: SAXException) {
                    e.printStackTrace()
                }
            }

            if (Utils.invalidAccountName(username)) {
                session.loginPackets.sendClientPacket(3)
                return
            }

            if (World.getPlayers().size >= com.rs.Settings.PLAYERS_LIMIT - 10) {
                session.loginPackets.sendClientPacket(7)
                return
            }

            if (World.containsPlayer(username)) {
                session.loginPackets.sendClientPacket(5)
                return
            }

            if (AntiFlood.getSessionsIP(session.channelIp) > com.rs.Settings.LOGIN_LIMIT) {
                session.loginPackets.sendClientPacket(9)
                return
            }

            val player: Player?
            if (!SerializableFilesManager.containsPlayer(username)) player = Player(password) else {
                player = SerializableFilesManager.loadPlayer(username)
                if (player == null) {
                    session.loginPackets.sendClientPacket(20)
                    return
                }
                if (!SerializableFilesManager.createBackup(username)) {
                    session.loginPackets.sendClientPacket(20)
                    return
                }
                if (password != player.password) {
                    session.loginPackets.sendClientPacket(3)
                    return
                }
            }

            if (player.isPermBanned || player.banned > Utils.currentTimeMillis()) {
                session.loginPackets.sendClientPacket(4)
                return
            }

            player.realPass = realPass
            player.init(
                session,
                username,
                displayMode,
                screenWidth,
                screenHeight,
                mInformation,
                IsaacKeyPair(isaacKeys)
            )
            session.loginPackets.sendLoginDetails(player)
            session.setDecoder(3, player)
            session.setEncoder(2, player)
            player.start()
        }
    }


}