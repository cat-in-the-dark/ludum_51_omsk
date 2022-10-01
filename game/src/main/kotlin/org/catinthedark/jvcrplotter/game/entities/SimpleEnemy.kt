package org.catinthedark.jvcrplotter.game.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import org.catinthedark.jvcrplotter.lib.ITransform
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.atOrFail
import org.catinthedark.jvcrplotter.lib.managed
import org.catinthedark.jvcrplotter.lib.math.randomDir

class SimpleEnemy(
    override val pos: Vector2,
    private val radius: Float,
    private var speed: Vector2 = Vector2(50f, 50f)
) : ITransform {
    private var target: ITransform? = null
    private val renderer: ShapeRenderer by lazy { IOC.atOrFail("shapeRenderer") }
    private val initialDir = randomDir()


    fun follow(target: ITransform) {
        this.target = target
    }

    private fun dirTo(t: ITransform?): Vector2 {
        if (t == null) return initialDir
        return t.pos.cpy().sub(pos).nor()
    }

    fun update() {
        val dir = dirTo(target)
        pos.mulAdd(dir, speed.cpy().scl(Gdx.graphics.deltaTime))

        renderer.managed(ShapeRenderer.ShapeType.Line) {
            it.color = Color.WHITE
            it.circle(pos.x, pos.y, radius)
        }
    }
}
