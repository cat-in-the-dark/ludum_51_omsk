package org.catinthedark.jvcrplotter.game.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import org.catinthedark.jvcrplotter.game.Const
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.atOrFail
import org.catinthedark.jvcrplotter.lib.interfaces.ITransform
import org.catinthedark.jvcrplotter.lib.interfaces.IUpdatable
import org.catinthedark.jvcrplotter.lib.managed
import org.catinthedark.jvcrplotter.lib.polygon2

class Tower(override val pos: Vector2) : ITransform, IUpdatable {
    private val render: ShapeRenderer by lazy { IOC.atOrFail("shapeRenderer") }

    private val topPoint = Vector2(
        pos.x, pos.y - Const.Balance.Tower.VISUAL_HEIGHT
    )

    private val beaconPoint = topPoint.cpy().add(0f, -65f)
    private val beaconTop = beaconPoint.cpy().add(0f, 30f)
    private val beaconBottom = beaconPoint.cpy().add(0f, -30f)
    private val beaconRadius = Vector2(0f, 20f)

    private var radius = Vector2(0f, Const.Balance.Tower.RADIUS)
    private val p1 = pos.cpy().add(radius.cpy().scl(1f, 0.5f))
    private val p2 = pos.cpy().add(radius.cpy().rotateDeg(120f).scl(1f, 0.5f))
    private val p3 = pos.cpy().add(radius.cpy().rotateDeg(240f).scl(1f, 0.5f))

    override fun update() {
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
            it.triangle(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y)
        }
        Gdx.gl.glDisable(GL20.GL_BLEND)

        render.managed(ShapeRenderer.ShapeType.Line) {
            it.color = Color.FIREBRICK
            it.triangle(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y)
            it.line(topPoint.x, topPoint.y, p1.x, p1.y)
            it.line(topPoint.x, topPoint.y, p2.x, p2.y)
            it.line(topPoint.x, topPoint.y, p3.x, p3.y)

            it.color = Color.YELLOW
            it.polygon2(bp1, bp2, bp3, bp4)
            for (point in points) {
                it.line(beaconTop, point)
                it.line(beaconBottom, point)
            }
        }
    }
}
