package org.catinthedark.alyoep.game.control

import com.badlogic.gdx.math.Vector2

abstract class PlayerController {
    private var startState = false
    abstract fun getDirection(): Vector2

    abstract fun isStartPressed(): Boolean

    fun isStartJustPressed(): Boolean {
        var res = false
        if (startState && !isStartPressed()) {
            res = true
        }
        startState = isStartPressed()

        return res
    }
}
