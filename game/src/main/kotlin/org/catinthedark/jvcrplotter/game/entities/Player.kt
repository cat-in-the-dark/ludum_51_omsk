package org.catinthedark.jvcrplotter.game.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.MathUtils.cos
import com.badlogic.gdx.math.MathUtils.sin
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import org.catinthedark.jvcrplotter.game.Const
import org.catinthedark.jvcrplotter.game.control.PlayerController
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.atOrFail
import org.catinthedark.jvcrplotter.lib.interfaces.ICollisionRect
import org.catinthedark.jvcrplotter.lib.interfaces.ITransform
import org.catinthedark.jvcrplotter.lib.managed
import org.catinthedark.jvcrplotter.lib.triangle2
import org.slf4j.LoggerFactory
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
    private val playerHeight = 48f
    private val playerWidth = 32f

    private val minVisualHeight = 40f
    private var visualHeight = 48f
    private var hpAlpha = 0.75f
    private var time = 0.0f

    private var isMoving = false
    private var moveTime = 0f

    val p1: Vector2
        get() = Vector2(pos.x + playerWidth / 2, pos.y)
    val p2: Vector2
        get() = Vector2(pos.x, pos.y + playerHeight)
    val p3: Vector2
        get() = Vector2(pos.x + playerWidth, pos.y + playerHeight)
    val exradius: Float
        get() = max(playerHeight / 2f, playerWidth / 2f)
    val center: Vector2
        get() = Vector2(pos.x + playerWidth / 2f, pos.y + playerHeight / 2f)

    private fun updatePos() {
        val dir = controller.getDirection()
        if (dir.len() > 1) {
            dir.nor()
        }

        if (dir.len() > 0.001) {
            isMoving = true
            moveTime += Gdx.graphics.deltaTime
        } else {
            isMoving = false
            moveTime = 0f
        }

        pos.add(dir.scl(Const.Balance.MAX_PLAYER_SPEED).scl(Gdx.graphics.deltaTime))
    }

    private fun draw() {
        val p2Cached = p2
        val p3Cached = p3

        visualHeight = if (isMoving) {
            MathUtils.map(
                -1f, 1f, minVisualHeight, playerHeight, cos(moveTime * 20)
            )
        } else {
            playerHeight
        }

        val p1Cached = p1.cpy().add(0f, visualHeight - playerHeight)

        render.managed(ShapeRenderer.ShapeType.Line) {
            it.color = color
            it.triangle(p1Cached.x, p1Cached.y, p2Cached.x, p2Cached.y, p3Cached.x, p3Cached.y)
        }

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        render.managed(ShapeRenderer.ShapeType.Filled) {
            val clr = Color(color)
            clr.a = hpAlpha
            val hpScale = 1f - (currentHP / stats.maxHP)
//            if (hpScale > 0.6f) {
//                clr.a = (sin(time * 20f) + 1f) / 2f * hpAlpha
//            } else {
//                clr.a = hpAlpha
//            }
            it.color = clr
            val p2Hp = p2Cached.cpy().sub(p1Cached).scl(hpScale).add(p1Cached)
            val p3Hp = p3Cached.cpy().sub(p1Cached).scl(hpScale).add(p1Cached)
            it.triangle2(p2Hp, p2Cached, p3Cached)
            it.triangle2(p2Hp, p3Hp, p3Cached)
        }
        Gdx.gl.glDisable(GL20.GL_BLEND)
    }

    fun update() {
        time += Gdx.graphics.deltaTime
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
