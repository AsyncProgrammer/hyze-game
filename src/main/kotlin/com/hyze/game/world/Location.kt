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

import com.hyze.utils.Settings
import com.rs.game.WorldTile
import com.rs.utils.Utils
import java.io.Serializable
import kotlin.math.abs


/**
 * DESCRIPTION
 *
 * @author var_5
 * @date 06/09/2020 at 19:30
 */
open class Location : Serializable{

    /**
     * Location coordinates
     */

    var x = 0
    var y = 0
    var plane = 0

    /**
     * Obtain the region coords from location
     */

    val regionX get() = x shr 6
    val regionY get() = y shr 6
    val regionId get() = (regionX shl 8) + regionY

    val xInRegion get() = x and 0x3F
    val yInRegion get() = y and 0x3F

    /**
     * Obtain the chunks values from location
     */

    val chunkX get() = x shr 3
    val chunkY get() = y shr 3

    val tileHash get() = y + (x shl 14) + (plane shl 28)
    val regionHash get() = regionY + (regionX shl 8) + (plane shl 16)

    constructor(x: Int, y: Int, plane: Int){
        this.x = x
        this.y = y
        this.plane = plane
    }

    constructor(location: Location){
        this.x = location.x
        this.y = location.y
        this.plane = location.plane
    }

    constructor(location: Location, randomize: Int){
        x = ((location.x + Utils.getRandom(randomize * 2) - randomize))
        y = ((location.y + Utils.getRandom(randomize * 2) - randomize))
        plane = location.plane
    }

    constructor(hash: Int) {
        x = (hash shr 14 and 0x3fff)
        y = (hash and 0x3fff)
        plane = (hash shr 28)
    }

    /**
     * If is within distance from a new tile
     *
     * @return is in distance
     */
    fun withinDistance(location: Location, distance: Int): Boolean {
        if (location.plane != plane) return false
        val deltaX = location.x - x
        val deltaY = location.y - y
        return deltaX <= distance && deltaX >= -distance && deltaY <= distance && deltaY >= -distance
    }

    fun withinDistance(location: Location): Boolean {
        return if (location.plane != plane) false else abs(location.x - x) <= 14 && abs(location.y - y) <= 14
    }

    /**
     * Plus new coords to a location
     */
    fun moveLocation(xOffset: Int, yOffset: Int, planeOffset: Int) {
        x += xOffset.toShort()
        y += yOffset.toShort()
        plane += planeOffset.toByte()
    }

    /**
     * Obtain the local width of a location
     *
     * @return local x
     */
    fun getLocalX(tile: Location, mapSize: Int): Int {
        return x - 8 * (tile.chunkX - (Settings.MAP_SIZES[mapSize] shr 4))
    }

    /**
     * Obtain the local height of a location
     *
     * @return local y
     */
    fun getLocalY(tile: Location, mapSize: Int): Int {
        return y - 8 * (tile.chunkY - (Settings.MAP_SIZES[mapSize] shr 4))
    }

    /**
     * Obtain the coord face X from location
     *
     * @return coord face X
     */
    fun getCoordFaceX(sizeX: Int): Int {
        return getCoordFaceX(sizeX, -1, -1)
    }

    /**
     * Obtain the coord face X from location
     *
     * @return coord face X
     */
    fun getCoordFaceX(sizeX: Int, sizeY: Int, rotation: Int): Int {
        return x + ((if (rotation == 1 || rotation == 3) sizeY else sizeX) - 1) / 2
    }

    /**
     * Obtain the coord face Y from location
     *
     * @return coord face Y
     */
    fun getCoordFaceY(sizeY: Int): Int {
        return getCoordFaceY(-1, sizeY, -1)
    }

    /**
     * Obtain the coord face Y from location
     *
     * @return coord face Y
     */
    fun getCoordFaceY(sizeX: Int, sizeY: Int, rotation: Int): Int {
        return y + ((if (rotation == 1 || rotation == 3) sizeX else sizeY) - 1) / 2
    }

    /**
     * Transform a location in a new object
     */
    fun transform(x: Int, y: Int, plane: Int): Location{
        return Location(x, y, plane)
    }

    companion object{
        fun getCoordFaceX(x: Int, sizeX: Int, sizeY: Int,
                          rotation: Int): Int {
            return x + ((if (rotation == 1 || rotation == 3) sizeY else sizeX) - 1) / 2
        }

        fun getCoordFaceY(y: Int, sizeX: Int, sizeY: Int,
                          rotation: Int): Int {
            return y + ((if (rotation == 1 || rotation == 3) sizeX else sizeY) - 1) / 2
        }
    }
}