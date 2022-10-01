package org.catinthedark.jvcrplotter.game.entities

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import org.catinthedark.jvcrplotter.game.Const
import org.catinthedark.jvcrplotter.game.control.PlayerController
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.atOrFail
import org.catinthedark.jvcrplotter.lib.managed

class Player(
    private var pos: Vector2,
    private val color: Color,
    private val controller: PlayerController
) {

    private val render: ShapeRenderer by lazy { IOC.atOrFail("shapeRenderer") }

    private var speed = Vector2(0f, 0f)

    private val playerHeight = 32f
    private val playerWidth = 24f

    private fun updatePos() {
        var dir = controller.getDirection()
        if (dir.len() == 0f) {
            dir = speed.cpy().nor().scl(-1 * Const.Balance.PLAYER_FRICTION)
        } else {
            dir.scl(Const.Balance.PLAYER_ACC)
        }
        speed.add(dir)
        if (speed.len() > Const.Balance.MAX_PLAYER_SPEED) {
            speed.nor().scl(Const.Balance.MAX_PLAYER_SPEED)
        } else if (speed.len() < Const.Balance.PLAYER_FRICTION) {
            speed.setZero()
        }

        pos.add(speed)
    }

    private fun draw() {
        render.managed(ShapeRenderer.ShapeType.Filled) {
            it.color = color
            it.triangle(
                pos.x,
                pos.y,
                pos.x - playerWidth / 2,
                pos.y + playerHeight,
                pos.x + playerWidth / 2,
                pos.y + playerHeight
            )
        }
    }

    fun update() {
        updatePos()
        draw()
    }
}
