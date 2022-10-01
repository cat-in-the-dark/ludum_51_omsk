package org.catinthedark.jvcrplotter.game.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.ITransform
import org.catinthedark.jvcrplotter.lib.atOrFail
import org.catinthedark.jvcrplotter.lib.managed

class Bullet(
    override val pos: Vector2,
    private val dir: Vector2,
    private val speed: Vector2 = Vector2(350f, 350f)
) : ITransform {
    private val size = 25f
    private val renderer: ShapeRenderer by lazy { IOC.atOrFail("shapeRenderer") }

    fun update() {
        pos.mulAdd(dir, speed.cpy().scl(Gdx.graphics.deltaTime))

        renderer.managed(ShapeRenderer.ShapeType.Line) {
            it.color = Color.RED
            it.line(pos.x, pos.y, pos.x + dir.x * size, pos.y + dir.y * size)
        }
    }
}
