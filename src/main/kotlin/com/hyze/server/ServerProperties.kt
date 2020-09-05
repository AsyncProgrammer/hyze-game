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

import java.io.InputStream
import java.lang.Exception
import java.util.*


/**
 * DESCRIPTION
 *
 * @author var_5
 * @date 05/09/2020 at 15:04
 */
object ServerProperties {

    private lateinit var properties: Properties

    fun loadProperty() : Properties{
        properties = Properties()

        val propertiesFile = "server.properties"
        val inputStream = javaClass.classLoader.getResourceAsStream(propertiesFile)

        properties.load(inputStream)
        return properties
    }

    fun getProperty(property: String) : String{
        return properties.getProperty(property)
    }
}