package org.catinthedark.jvcrplotter.game.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import org.catinthedark.jvcrplotter.game.Const
import org.catinthedark.jvcrplotter.game.control.PlayerController
import org.catinthedark.jvcrplotter.lib.interfaces.ICollisionRect
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.atOrFail
import org.catinthedark.jvcrplotter.lib.interfaces.ITransform
import org.catinthedark.jvcrplotter.lib.managed
import org.slf4j.LoggerFactory
import kotlin.math.log
import kotlin.math.max

data class Stats(var bulletsCount: Int, var maxHP: Float)

class Player(
    override val pos: Vector2,
    private val color: Color,
    private val controller: PlayerController,
    val stats: Stats = Stats(bulletsCount = 1, maxHP = 16f),
) : ITransform, ICollisionRect {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val render: ShapeRenderer by lazy { IOC.atOrFail("shapeRenderer") }

    private var currentHP: Float = stats.maxHP
    private val playerHeight = 32f
    private val playerWidth = 24f

    val p1: Vector2
        get() = Vector2(pos.x + playerWidth / 2, pos.y)
    val p2: Vector2
        get() = Vector2(pos.x, pos.y + playerHeight)
    val p3: Vector2
        get() = Vector2(pos.x + playerWidth, pos.y + playerHeight)
    val exradius: Float
        get() = max(playerHeight / 2f, playerWidth / 2f)

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
            val p1Cached = p1
            val p2Cached = p2
            val p3Cached = p3
            it.triangle(p1Cached.x, p1Cached.y, p2Cached.x, p2Cached.y, p3Cached.x, p3Cached.y)
        }
    }

    fun update() {
        updatePos()
        draw()
    }

    override fun getCollisionRect(): Rectangle {
        return Rectangle(pos.x, pos.y, playerWidth, playerHeight)
    }

    fun hit(damage: Float) {
        logger.info("HIT dmg=$damage hp=$currentHP")
        currentHP -= damage
        if (currentHP <= 0) {
            // TODO: die
            logger.info("DIED FROM CRINGE")
            currentHP = 0f
        }
    }
}
