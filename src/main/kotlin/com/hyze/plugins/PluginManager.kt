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

package com.hyze.plugins

import com.hyze.events.Event
import com.hyze.events.EventHandler
import com.hyze.events.Listener


/**
 * DESCRIPTION
 *
 * @author var_5
 * @date 29/07/2020 at 11:19
 */
class PluginManager {

    fun dispatchEvent(listener: Listener){
        val methods = listener.javaClass.methods

        for(method in methods){
            if(method.isAnnotationPresent(EventHandler::class.java)){
                method.invoke(listener.javaClass.declaredClasses[0].newInstance())
            }
        }

    }

}