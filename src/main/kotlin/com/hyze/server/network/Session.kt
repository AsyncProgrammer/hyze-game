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

package com.hyze.server.network

import com.hyze.server.network.decoders.*
import com.hyze.server.network.encoders.Encoder
import com.hyze.server.network.encoders.GrabPacketsEncoder
import com.hyze.server.network.encoders.LoginPacketsEncoder
import com.hyze.server.network.encoders.WorldPacketsEncoder
import com.rs.game.player.Player
import com.rs.io.OutputStream
import com.rs.utils.IPBanL
import org.jboss.netty.buffer.ChannelBuffer
import org.jboss.netty.buffer.ChannelBuffers
import org.jboss.netty.channel.Channel
import org.jboss.netty.channel.ChannelFuture


/**
 * DESCRIPTION
 *
 * @author var_5
 * @date 06/09/2020 at 16:29
 */
class Session(var channel: Channel) {

    var decoder: Decoder? = null
    var encoder: Encoder? = null

    val grabPackets get() = encoder as GrabPacketsEncoder
    val loginPackets get() = encoder as LoginPacketsEncoder
    val worldPackets get() = encoder as WorldPacketsEncoder

    val grabPacketsDecoder get() = decoder as GrabPacketsDecoder

    val channelIp get() = channel.remoteAddress.toString().split(":").toTypedArray()[0].replace("/", "")

    init {
        if(IPBanL.isBanned(channelIp)){
            channel.disconnect()
        }
        setDecoder(0)
    }

    fun setDecoder(stage: Int, attachment: Any?){
        decoder = when(stage){
            0 -> ClientPacketsDecoder(this)
            1 -> GrabPacketsDecoder(this)
            2 -> LoginPacketsDecoder(this)
            3 -> WorldPacketsDecoder(this, attachment as Player)
            else -> null
        }
    }

    fun setEncoder(stage: Int, attachment: Any?){
        encoder = when(stage){
            0 -> GrabPacketsEncoder(this)
            1 -> LoginPacketsEncoder(this)
            2 -> WorldPacketsEncoder(this, attachment as Player?)
            else -> null
        }
    }

    fun write(outStream: OutputStream): ChannelFuture? {
        if (channel.isConnected) {
            val buffer = ChannelBuffers.copiedBuffer(
                    outStream.buffer, 0, outStream.offset)
            synchronized(channel) { return channel.write(buffer) }
        }
        return null
    }

    fun write(outStream: ChannelBuffer?): ChannelFuture? {
        if (outStream == null) return null
        if (channel.isConnected) {
            synchronized(channel) { return channel.write(outStream) }
        }
        return null
    }

    fun setDecoder(stage: Int){
        setDecoder(stage, null)
    }

    fun setEncoder(stage: Int){
        setEncoder(stage, null)
    }
}