package org.catinthedark.jvcrplotter.game.entities.powerups

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import org.catinthedark.jvcrplotter.game.entities.Player
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.atOrFail
import org.catinthedark.jvcrplotter.lib.interfaces.ICollisionRect
import org.catinthedark.jvcrplotter.lib.interfaces.ITransform
import org.catinthedark.jvcrplotter.lib.interfaces.IUpdatable
import org.catinthedark.jvcrplotter.lib.managed
import org.catinthedark.jvcrplotter.lib.polygon2

abstract class PowerUp(override val pos: Vector2, val color: Color) : ITransform, ICollisionRect, IUpdatable {
    private val renderer: ShapeRenderer by lazy { IOC.atOrFail("shapeRenderer") }

    private val size = 32f
    private val radius = Vector2(size / 2, 0f)

    override fun update() {
        radius.rotateDeg(90f * Gdx.graphics.deltaTime)
        val topCenter = pos.cpy().add(size / 2, size / 4)

        val top = listOf(
            topCenter.cpy().add(radius.cpy().scl(1f, 0.5f)),
            topCenter.cpy().add(radius.cpy().rotateDeg(90f).scl(1f, 0.5f)),
            topCenter.cpy().add(radius.cpy().rotateDeg(180f).scl(1f, 0.5f)),
            topCenter.cpy().add(radius.cpy().rotateDeg(270f).scl(1f, 0.5f))
        )

        val bottom = top.map {
            it.cpy().add(0f, size * 0.75f)
        }

        renderer.managed(ShapeRenderer.ShapeType.Line) {
            it.color = color
            it.polygon2(top)
            it.polygon2(bottom)
            for (i in 0 until 4) {
                it.line(top[i], bottom[i])
            }

//            it.rect(pos.x, pos.y, size, size)
        }
    }

    override fun getCollisionRect(): Rectangle {
        return Rectangle(pos.x, pos.y, size, size)
    }

    abstract fun apply(player: Player)
}
