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

package com.hyze.events

import com.google.common.collect.Maps
import java.util.*
import java.util.concurrent.ConcurrentMap
import kotlin.reflect.KClass


/**
 * DESCRIPTION
 *
 * @author var_5
 * @date 08/08/2020 at 14:17
 */
class EventManager {

    val eventRegistry: ConcurrentMap<KClass<out Event>, EventExecutor> = Maps.newConcurrentMap()

    fun callEvent(event: Event){
        eventRegistry[event::class]?.execute()
    }

}