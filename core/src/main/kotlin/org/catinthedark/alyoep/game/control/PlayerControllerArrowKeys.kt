package org.catinthedark.alyoep.game.control

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2

class PlayerControllerArrowKeys : PlayerController() {
    override fun getDirection(): Vector2 {
        val vertical = if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            -1f
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            1f
        } else {
            0f
        }

        val horizontal = if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            -1f
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            1f
        } else {
            0f
        }

        return Vector2(horizontal, vertical).nor()
    }

    override fun isStartPressed(): Boolean {
        return Gdx.input.isKeyPressed(Input.Keys.ENTER)
    }
}
