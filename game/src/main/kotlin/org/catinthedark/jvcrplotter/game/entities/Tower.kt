package org.catinthedark.jvcrplotter.game.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import org.catinthedark.jvcrplotter.game.Const
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.atOrFail
import org.catinthedark.jvcrplotter.lib.interfaces.IDestructible
import org.catinthedark.jvcrplotter.lib.interfaces.ITransform
import org.catinthedark.jvcrplotter.lib.interfaces.IUpdatable
import org.catinthedark.jvcrplotter.lib.managed
import org.catinthedark.jvcrplotter.lib.polygon2
import org.slf4j.LoggerFactory

class Tower(override val pos: Vector2, override var shouldDestroy: Boolean = false) : ITransform, IUpdatable,
    IDestructible {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val render: ShapeRenderer by lazy { IOC.atOrFail("shapeRenderer") }

    private val topPoint = Vector2(
        pos.x, pos.y - Const.Balance.Tower.VISUAL_HEIGHT
    )

    private var currentHP = Const.Balance.Tower.RADIUS
    private val beaconPoint = topPoint.cpy().add(0f, -65f)
    private val beaconTop = beaconPoint.cpy().add(0f, 30f)
    private val beaconBottom = beaconPoint.cpy().add(0f, -30f)
    private val beaconRadius = Vector2(0f, 20f)

    private var radius = Vector2(0f, Const.Balance.Tower.RADIUS)
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
        val p1Cached = p1
        val p2Cached = p2
        val p3Cached = p3

        beaconRadius.rotateDeg(90f * Gdx.graphics.deltaTime)
        val bp1 = beaconPoint.cpy().add(beaconRadius.cpy().scl(1f, 0.5f))
        val bp2 = beaconPoint.cpy().add(beaconRadius.cpy().rotateDeg(90f).scl(1f, 0.5f))
        val bp3 = beaconPoint.cpy().add(beaconRadius.cpy().rotateDeg(180f).scl(1f, 0.5f))
        val bp4 = beaconPoint.cpy().add(beaconRadius.cpy().rotateDeg(270f).scl(1f, 0.5f))
        val points = listOf(bp1, bp2, bp3, bp4)

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        render.managed(ShapeRenderer.ShapeType.Filled) {
            val color = Color.FIREBRICK
            color.a = 0.3f
            it.color = color
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
        currentHP -= damage
        logger.info("DMG ${damage}, HP $currentHP")
        if (currentHP <= 0) {
            currentHP = 0f
        }
    }
}
