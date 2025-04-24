package org.catinthedark.alyoep.game.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import org.catinthedark.alyoep.game.Const
import org.catinthedark.alyoep.lib.*
import org.catinthedark.alyoep.lib.interfaces.IDestructible
import org.catinthedark.alyoep.lib.interfaces.ITransform
import org.catinthedark.alyoep.lib.interfaces.IUpdatable

class Tower(override val pos: Vector2, override var shouldDestroy: Boolean = false) : ITransform, IUpdatable,
    IDestructible {

    private val render: ShapeRenderer by lazy { IOC.atOrFail("shapeRenderer") }

    private val topPoint = Vector2(
        pos.x, pos.y - Const.Balance.Tower.VISUAL_HEIGHT
    )

    var currentHP = Const.Balance.Tower.MAX_HP
    private val beaconPoint = topPoint.cpy().add(0f, -65f)
    private val beaconTop = beaconPoint.cpy().add(0f, 30f)
    private val beaconBottom = beaconPoint.cpy().add(0f, -30f)
    private val beaconRadius = Vector2(0f, 20f)

    private var radius = Vector2(0f, Const.Balance.Tower.RADIUS)

    private var drawDamage = false
    private var drawDamageTime = 0f

    val p1: Vector2
        get() = pos.cpy().add(radius.cpy().scl(1f, 0.5f))
    val p2: Vector2
        get() = pos.cpy().add(radius.cpy().rotateDeg(120f).scl(1f, 0.5f))
    val p3: Vector2
        get() = pos.cpy().add(radius.cpy().rotateDeg(240f).scl(1f, 0.5f))

    override fun update() {
        draw()
    }

    private fun draw() {
        if (drawDamage) {
            drawDamageTime += Gdx.graphics.deltaTime
        }

        val p1Cached = p1
        val p2Cached = p2
        val p3Cached = p3

        beaconRadius.rotateDeg(90f * Gdx.graphics.deltaTime)
        val bp1 = beaconPoint.cpy().add(beaconRadius.cpy().scl(1f, 0.5f))
        val bp2 = beaconPoint.cpy().add(beaconRadius.cpy().rotateDeg(90f).scl(1f, 0.5f))
        val bp3 = beaconPoint.cpy().add(beaconRadius.cpy().rotateDeg(180f).scl(1f, 0.5f))
        val bp4 = beaconPoint.cpy().add(beaconRadius.cpy().rotateDeg(270f).scl(1f, 0.5f))
        val points = listOf(bp1, bp2, bp3, bp4)

        if (drawDamage) {
            points.forEach {
                val jitterX = MathUtils.map(0f, 1f, -2f, 2f, MathUtils.random())
                val jitterY = MathUtils.map(0f, 1f, -2f, 2f, MathUtils.random())
                it.add(Vector2(jitterX, jitterY))
            }
        }

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        render.managed(ShapeRenderer.ShapeType.Filled) {
            val color = Color(Color.FIREBRICK)
            if (drawDamage) {
                val arg = drawDamageTime * MathUtils.PI * 4
                color.a = MathUtils.sin(arg) * 0.5f
                if (arg > MathUtils.PI) {
                    drawDamage = false
                }
            } else {
                color.a = 0.5f
                drawDamageTime = 0f
            }
            it.color = color

            val hpScale = 1f - currentHP / Const.Balance.Tower.MAX_HP

            val p2Hp = p2Cached.cpy().sub(topPoint).scl(hpScale).add(topPoint)
            val p3Hp = p3Cached.cpy().sub(topPoint).scl(hpScale).add(topPoint)
            it.triangle2(p2Hp, p2Cached, p3Cached)
            it.triangle2(p2Hp, p3Hp, p3Cached)

            it.triangle(
                p1Cached.x,
                p1Cached.y,
                p2Cached.x,
                p2Cached.y,
                p3Cached.x,
                p3Cached.y
            )

//            it.triangle(topPoint.x, topPoint.y, )
        }
        Gdx.gl.glDisable(GL20.GL_BLEND)

        render.managed(ShapeRenderer.ShapeType.Line) {
            it.color = Color.FIREBRICK
            it.triangle(
                p1Cached.x,
                p1Cached.y,
                p2Cached.x,
                p2Cached.y,
                p3Cached.x,
                p3Cached.y
            )
            it.line(topPoint.x, topPoint.y, p1Cached.x, p1Cached.y)
            it.line(topPoint.x, topPoint.y, p2Cached.x, p2Cached.y)
            it.line(topPoint.x, topPoint.y, p3Cached.x, p3Cached.y)

            it.color = Color.YELLOW
            it.polygon2(bp1, bp2, bp3, bp4)
            for (point in points) {
                it.line(beaconTop, point)
                it.line(beaconBottom, point)
            }
        }
    }

    fun hit(damage: Float) {
        drawDamage = true
        currentHP -= damage
        if (currentHP <= 0) {
            currentHP = 0f
        }
    }
}
