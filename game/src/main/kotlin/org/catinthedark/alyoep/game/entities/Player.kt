package org.catinthedark.alyoep.game.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.MathUtils.cos
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import org.catinthedark.alyoep.game.Const
import org.catinthedark.alyoep.game.control.PlayerController
import org.catinthedark.alyoep.lib.IOC
import org.catinthedark.alyoep.lib.atOrFail
import org.catinthedark.alyoep.lib.interfaces.ICollisionRect
import org.catinthedark.alyoep.lib.interfaces.IDestructible
import org.catinthedark.alyoep.lib.interfaces.ITransform
import org.catinthedark.alyoep.lib.managed
import org.catinthedark.alyoep.lib.triangle2
import org.slf4j.LoggerFactory
import kotlin.math.max
import kotlin.math.pow

data class NovaStats(
    var novaFreq: Int = Const.Balance.PowerUp.MIN_NOVA_FREQ,
    var novaDmg: Float = 10f,
    var novaDuration: Float = 0.5f,
    var novaSpeed: Float = 360f,
)

data class Stats(
    var bulletsCount: Int,
    var bulletsFireSpeed: Int,
    var maxHP: Float,
    var bulletDamage: Float,
    var nova: NovaStats? = null
)

class Player(
    override val pos: Vector2,
    private val color: Color,
    private val controller: PlayerController,
    val stats: Stats = Stats(
        bulletsCount = 1,
        bulletsFireSpeed = 1,
        maxHP = 16f,
        bulletDamage = 1.1f,
    ),
    override var shouldDestroy: Boolean = false,
) : ITransform, ICollisionRect, IDestructible {
    var playHealAnimation: Boolean = false
        set(value) {
            field = value
            healAnimationTime = 0f
        }
    var healAnimationTime: Float = 0f
    private val logger = LoggerFactory.getLogger(javaClass)
    private val render: ShapeRenderer by lazy { IOC.atOrFail("shapeRenderer") }

    var currentHP: Float = stats.maxHP
    val height = 48f
    val width = 32f

    private val minVisualHeight = 40f
    private var visualHeight = 48f
    private var hpAlpha = 0.75f
    private var time = 0.0f

    private var isMoving = false
    private var moveTime = 0f
    private var fallTime = 0f

    var nova: Nova? = null
    var healNova: HealNova? = null

    val p1: Vector2
        get() = Vector2(pos.x + width / 2, pos.y)
    val p2: Vector2
        get() = Vector2(pos.x, pos.y + height)
    val p3: Vector2
        get() = Vector2(pos.x + width, pos.y + height)
    val exradius: Float
        get() = max(height / 2f, width / 2f)
    val center: Vector2
        get() = Vector2(pos.x + width / 2f, pos.y + height / 2f)

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

        val delta = dir.scl(Const.Balance.MAX_PLAYER_SPEED).scl(Gdx.graphics.deltaTime)
        pos.add(delta)
        if (pos.x < 0 || pos.y < 0 || pos.x + width > Const.Screen.WIDTH || pos.y + height > Const.Screen.HEIGHT) {
            pos.sub(delta)
        }
    }

    private fun draw() {
        val p2Cached = p2
        val p3Cached = p3

        visualHeight = if (isMoving) {
            MathUtils.map(
                -1f, 1f, minVisualHeight, height, cos(moveTime * 20)
            )
        } else {
            height
        }

        val p1Cached = p1.cpy().add(0f, visualHeight - height)

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

            if (playHealAnimation) {
                val colorBottom = Color(Color.CHARTREUSE)
                val colorTop = Color(Color.CHARTREUSE)
                colorTop.a = 0f

                val effectHeight = height * 2
                val tm = healAnimationTime * 1.5f
                val heightProgress = MathUtils.clamp(tm, 0f, 1f).pow(1f / 2) * effectHeight
                val alphaProgress = MathUtils.map(0f, 1f, 1f, 0f, MathUtils.clamp(tm, 0f, 1f).pow(3f))

                colorBottom.a = alphaProgress

                it.rect(
                    p2Cached.x,
                    p2Cached.y - heightProgress,
                    width,
                    heightProgress,
                    colorTop,
                    colorTop,
                    colorBottom,
                    colorBottom
                )
            }
        }
        Gdx.gl.glDisable(GL20.GL_BLEND)
    }

    private fun drawFallAnimation() {
        val p2Cached = p2
        val p3Cached = p3
        val p1Cached = p1

        val fallProgress = MathUtils.clamp(fallTime.toDouble().pow(4.0).toFloat(), 0f, 1f);
        val rotation = MathUtils.map(0f, 1f, 0f, 75f, fallProgress)
        val p1NewPos = p1Cached.cpy().sub(p2Cached).rotateDeg(rotation).add(p2Cached)

        render.managed(ShapeRenderer.ShapeType.Line) {
            it.color = color
            it.triangle2(p1NewPos, p2Cached, p3Cached)
        }
    }

    private fun updateNova() {
        nova?.also {
            it.update()
            if (it.shouldDestroy) {
                nova = null
            }
        }

        healNova?.also {
            it.update()
            if (it.shouldDestroy) {
                healNova = null
            }
        }
    }

    fun update() {
        time += Gdx.graphics.deltaTime
        if (playHealAnimation) {
            healAnimationTime += Gdx.graphics.deltaTime
            if (healAnimationTime > 1.0f) {
                playHealAnimation = false
            }
        }
        updateNova()
        if (currentHP > 0) {
            updatePos()
            draw()
        } else {
            fallTime += Gdx.graphics.deltaTime
            drawFallAnimation()
            if (fallTime >= 1.5f) {
                shouldDestroy = true
            }
        }
    }

    override fun getCollisionRect(): Rectangle {
        return Rectangle(pos.x, pos.y, width, height)
    }

    fun hit(damage: Float) {
//        logger.info("HIT dmg=$damage hp=$currentHP")
        currentHP -= damage
        if (currentHP <= 0) {
//            logger.info("DIED FROM CRINGE")
            currentHP = 0f
        }
    }
}
