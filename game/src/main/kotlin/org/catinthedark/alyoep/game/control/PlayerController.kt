package org.catinthedark.alyoep.game.control

import com.badlogic.gdx.math.Vector2

interface PlayerController {
    fun getDirection(): Vector2

    fun isStartPressed(): Boolean
}