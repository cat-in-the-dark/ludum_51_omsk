package org.catinthedark.jvcrplotter.game.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import org.catinthedark.jvcrplotter.lib.*
import org.catinthedark.jvcrplotter.lib.interfaces.IDestructible
import org.catinthedark.jvcrplotter.lib.interfaces.IUpdatable
import kotlin.math.max

class HealNova(
    val center: Vector2,
    var radius: Float,
    val speed: Float = 2000f,
) : IUpdatable, IDestructible {
    val lifetime = OnceBarrier(0.8f)
    private val render: ShapeRenderer by lazy { IOC.atOrFail("shapeRenderer") }
    private val color = Color.LIME

    val r: Vector2
        get() = Vector2(0f, -radius)
    val p1: Vector2
        get() = center.cpy().add(r)
    val p2: Vector2
        get() = center.cpy().add(r.cpy().rotateDeg(120f))
    val p3: Vector2
        get() = center.cpy().add(r.cpy().rotateDeg(240f))

    override fun update() {
        if (shouldDestroy) return

        lifetime.invoke { shouldDestroy = true }

        radius += speed * Gdx.graphics.deltaTime

        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        render.managed(ShapeRenderer.ShapeType.Filled) {
            val clr = Color(color)
            clr.a = max(lifetime.timeAmount, 0.1f)

            it.color = clr
            it.triangle2(p1, p2, p3)
        }
        Gdx.gl.glDisable(GL20.GL_BLEND)
    }

    override var shouldDestroy: Boolean = false
}
