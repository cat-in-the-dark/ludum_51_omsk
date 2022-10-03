package org.catinthedark.alyoep.game.control

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2

class PlayerControllerWasd : PlayerController {
    override fun getDirection(): Vector2 {
        val vertical = if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            -1f
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            1f
        } else {
            0f
        }

        val horizontal = if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            -1f
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            1f
        } else {
            0f
        }

        return Vector2(horizontal, vertical).nor()
    }

    override fun isStartPressed(): Boolean {
        return Gdx.input.isKeyPressed(Input.Keys.SPACE)
    }
}
