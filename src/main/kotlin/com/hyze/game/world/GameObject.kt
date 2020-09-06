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

package com.hyze.game.world

import com.rs.cache.loaders.ObjectDefinitions


/**
 * DESCRIPTION
 *
 * @author var_5
 * @date 06/09/2020 at 19:56
 */
class GameObject: Location{

    var id = 0
    var type = 0
    var rotation = 0
    var life = 0

    /**
     * Obtains the object definitions
     */
    val definitions: ObjectDefinitions get() = ObjectDefinitions.getObjectDefinitions(id)

    constructor(x: Int, y: Int, plane: Int, id: Int, type: Int, rotation: Int, life: Int) : super(x, y, plane) {
        this.id = id
        this.type = type
        this.rotation = rotation
        this.life = life
    }

    constructor(location: Location, id: Int, type: Int, rotation: Int, life: Int) : super(location) {
        this.id = id
        this.type = type
        this.rotation = rotation
        this.life = life
    }

    constructor(location: Location, randomize: Int, id: Int, type: Int, rotation: Int, life: Int) : super(location, randomize) {
        this.id = id
        this.type = type
        this.rotation = rotation
        this.life = life
    }

    constructor(hash: Int, id: Int, type: Int, rotation: Int, life: Int) : super(hash) {
        this.id = id
        this.type = type
        this.rotation = rotation
        this.life = life
    }
    
}