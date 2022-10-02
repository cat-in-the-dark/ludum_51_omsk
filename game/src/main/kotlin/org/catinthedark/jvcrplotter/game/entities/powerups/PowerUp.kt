package org.catinthedark.jvcrplotter.game.entities.powerups

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import org.catinthedark.jvcrplotter.game.entities.Player
import org.catinthedark.jvcrplotter.lib.interfaces.ICollisionRect
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.atOrFail
import org.catinthedark.jvcrplotter.lib.interfaces.ITransform
import org.catinthedark.jvcrplotter.lib.managed

abstract class PowerUp(override val pos: Vector2) : ITransform, ICollisionRect {
    private val renderer: ShapeRenderer by lazy { IOC.atOrFail("shapeRenderer") }

    private val size = 24f

    fun update() {
        renderer.managed(ShapeRenderer.ShapeType.Line) {
            it.color = Color.LIGHT_GRAY
            it.rect(pos.x, pos.y, size, size)
        }
    }

    override fun getCollisionRect(): Rectangle {
        return Rectangle(pos.x, pos.y, size, size)
    }

    abstract fun apply(player: Player)
}
