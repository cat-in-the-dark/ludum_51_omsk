package org.catinthedark.jvcrplotter.game.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import org.catinthedark.jvcrplotter.game.Const
import org.catinthedark.jvcrplotter.game.control.PlayerController
import org.catinthedark.jvcrplotter.lib.*

data class Stats(var bulletsCount: Int)

class Player(
    override val pos: Vector2,
    private val color: Color,
    private val controller: PlayerController
) : ITransform, ICollisionRect {
    private val render: ShapeRenderer by lazy { IOC.atOrFail("shapeRenderer") }

    public val stats = Stats(bulletsCount = 1)

    private val playerHeight = 32f
    private val playerWidth = 24f

    private fun updatePos() {
        val dir = controller.getDirection()
        if (dir.len() > 1) {
            dir.nor()
        }
        pos.add(dir.scl(Const.Balance.MAX_PLAYER_SPEED).scl(Gdx.graphics.deltaTime))
    }

    private fun draw() {
        render.managed(ShapeRenderer.ShapeType.Filled) {
            it.color = color
            it.triangle(
                pos.x + playerWidth / 2,
                pos.y,
                pos.x,
                pos.y + playerHeight,
                pos.x + playerWidth,
                pos.y + playerHeight
            )
        }
    }

    fun update() {
        updatePos()
        draw()
    }

    override fun getCollisionRect(): Rectangle {
        return Rectangle(pos.x, pos.y, playerWidth, playerHeight)
    }
}
