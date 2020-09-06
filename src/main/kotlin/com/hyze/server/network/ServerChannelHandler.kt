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

import com.hyze.server.network.decoders.WorldPacketsDecoder
import com.hyze.utils.Settings
import com.rs.cores.CoresManager
import com.rs.io.InputStream
import com.rs.net.Session
import org.jboss.netty.bootstrap.ServerBootstrap
import org.jboss.netty.buffer.ChannelBuffer
import org.jboss.netty.channel.ChannelHandlerContext
import org.jboss.netty.channel.ChannelStateEvent
import org.jboss.netty.channel.MessageEvent
import org.jboss.netty.channel.SimpleChannelHandler
import org.jboss.netty.channel.group.ChannelGroup
import org.jboss.netty.channel.group.DefaultChannelGroup
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import java.lang.Exception
import java.net.InetSocketAddress


/**
 * DESCRIPTION
 *
 * @author var_5
 * @date 05/09/2020 at 20:34
 */
class ServerChannelHandler: SimpleChannelHandler() {

    var channels: ChannelGroup? = null
    var bootstrap: ServerBootstrap? = null

    init {
        channels = DefaultChannelGroup()
        bootstrap = ServerBootstrap(NioServerSocketChannelFactory(
                CoresManager.serverBossChannelExecutor,
                CoresManager.serverWorkerChannelExecutor,
                CoresManager.serverWorkersCount
        ))

        bootstrap?.pipeline?.addLast("handler", this)
        bootstrap?.setOption("child.tcpNoDelay", true)
        bootstrap?.setOption("child.TcpAckFrequency", true)
        bootstrap?.setOption("chield.keepAlive", true)

        try {
            bootstrap?.bind(InetSocketAddress(Settings.PORT_ID))
        }catch (exception: Exception){
            Settings.PORT_ID += 1
            bootstrap?.bind(InetSocketAddress(Settings.PORT_ID))
        }
    }

    override fun channelOpen(ctx: ChannelHandlerContext?, e: ChannelStateEvent?) {
        channels?.add(e?.channel)
    }

    override fun channelClosed(ctx: ChannelHandlerContext?, e: ChannelStateEvent?) {
        channels?.remove(e?.channel)
    }

    override fun channelConnected(ctx: ChannelHandlerContext?, e: ChannelStateEvent?) {
        ctx?.attachment = Session(e?.channel)
    }

    override fun channelDisconnected(ctx: ChannelHandlerContext?, e: ChannelStateEvent?) {
        val sessionObject = ctx?.attachment

        if(sessionObject != null && sessionObject is Session){
            if(sessionObject.decoder is WorldPacketsDecoder){
                sessionObject.worldPackets.player.finish()
            }
        }
    }

    override fun messageReceived(ctx: ChannelHandlerContext?, e: MessageEvent?) {
        if(e?.message !is ChannelBuffer) return

        val sessionObject = ctx?.attachment

        if(sessionObject is Session){
            val session = sessionObject
            val buffer = e.message as ChannelBuffer
            buffer.markReaderIndex()
            val avail = buffer.readableBytes()

            if(avail < 1 || avail > Settings.RECEIVE_DATA_LIMIT) return

            val bufferArray = ByteArray(avail)
            buffer.readBytes(bufferArray)

            try {
                session.decoder.decode(InputStream(bufferArray))
            }catch (error: Throwable){
                error.printStackTrace()
            }
        }
    }

    fun shutdown(){
        channels?.close()?.awaitUninterruptibly()
        bootstrap?.releaseExternalResources()
    }

}