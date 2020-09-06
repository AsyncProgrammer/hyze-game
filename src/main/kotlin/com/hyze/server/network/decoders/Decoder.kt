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
import com.rs.io.InputStream


/**
 * DESCRIPTION
 *
 * @author var_5
 * @date 05/09/2020 at 14:53
 */
abstract class Decoder(val session: Session) {

    abstract fun decode(session: Session, stream: InputStream)

}