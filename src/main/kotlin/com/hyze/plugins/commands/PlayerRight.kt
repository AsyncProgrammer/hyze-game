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

package com.hyze.plugins.commands


/**
 * DESCRIPTION
 *
 * @author var_5
 * @date 19/07/2020 at 18:36
 */
enum class PlayerRight(val rightId: Int) {

    ADMIN(2),
    MODERATOR(1),
    SUPPORT(4),
    DONATOR(18),
    NORMAL(0)


}