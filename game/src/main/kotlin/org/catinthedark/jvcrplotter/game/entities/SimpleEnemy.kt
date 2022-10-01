package org.catinthedark.jvcrplotter.game.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import org.catinthedark.jvcrplotter.game.randomDir
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.atOrFail
import org.catinthedark.jvcrplotter.lib.managed

class SimpleEnemy(
    private var pos: Vector2,
    private val radius: Float,
    private var speed: Vector2 = Vector2(50f, 50f)
) {
    private var target: Vector2? = null
    private val renderer: ShapeRenderer by lazy { IOC.atOrFail("shapeRenderer") }

    fun follow(target: Vector2) {
        this.target = target
    }

    private fun dirToTarget(): Vector2 {
        val t = target?.cpy()
        return if (t != null) {
            t.sub(pos).nor()
        } else {
            randomDir()
        }
    }

    fun update() {
        val dir = dirToTarget()
        pos.mulAdd(dir, speed.cpy().scl(Gdx.graphics.deltaTime))

        renderer.managed(ShapeRenderer.ShapeType.Line) {
            it.color = Color.WHITE
            it.circle(pos.x, pos.y, radius)
        }
    }
}
