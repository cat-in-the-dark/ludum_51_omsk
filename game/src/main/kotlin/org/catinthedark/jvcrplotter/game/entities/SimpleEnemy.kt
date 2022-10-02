package org.catinthedark.jvcrplotter.game.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Vector2
import org.catinthedark.jvcrplotter.lib.*
import org.catinthedark.jvcrplotter.lib.interfaces.IDestructible
import org.catinthedark.jvcrplotter.lib.interfaces.ITransform
import org.catinthedark.jvcrplotter.lib.interfaces.IUpdatable
import org.catinthedark.jvcrplotter.lib.math.randomDir

class SimpleEnemy(
    override val pos: Vector2,
    val radius: Float,
    val damage: Float,
    private val hitCooldownTime: Float,
    private var speed: Vector2 = Vector2(50f, 50f)
) : ITransform, IUpdatable, IDestructible {
    override var shouldDestroy = false
    private var target: ITransform? = null
    private val renderer: ShapeRenderer by lazy { IOC.atOrFail("shapeRenderer") }
    private val initialDir = randomDir()
    private val hitCooldown = CoolDown(hitCooldownTime)

    val body: Circle
        get() = Circle(pos.x, pos.y, radius)

    fun follow(target: ITransform) {
        this.target = target
    }

    private fun dirTo(t: ITransform?): Vector2 {
        if (t == null) return initialDir
        return t.pos.cpy().sub(pos).nor()
    }

    override fun update() {
        val dir = dirTo(target)
        pos.mulAdd(dir, speed.cpy().scl(Gdx.graphics.deltaTime))

        renderer.managed(ShapeRenderer.ShapeType.Line) {
            it.color = Color.WHITE
            it.circle(pos.x, pos.y, radius)
        }

        hitCooldown.update()
    }

    fun damage(bullet: Bullet) {
        shouldDestroy = true
    }

    fun tryHitPlayer(player: Player, func: () -> Unit) {
        hitCooldown.invoke { func() }
    }
}
