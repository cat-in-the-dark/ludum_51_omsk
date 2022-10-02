package org.catinthedark.jvcrplotter.game.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Vector2
import org.catinthedark.jvcrplotter.lib.CoolDown
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.atOrFail
import org.catinthedark.jvcrplotter.lib.interfaces.IDestructible
import org.catinthedark.jvcrplotter.lib.interfaces.ITransform
import org.catinthedark.jvcrplotter.lib.interfaces.IUpdatable
import org.catinthedark.jvcrplotter.lib.managed
import org.catinthedark.jvcrplotter.lib.math.randomDir

class SimpleEnemy(
    override val pos: Vector2,
    val radius: Float,
    val damage: Float,
    private var hp: Float,
    val speed: Vector2,
    val isBoss: Boolean,
    private val hitCooldownTime: Float
) : ITransform, IUpdatable, IDestructible {
    override var shouldDestroy = false
    private var target: ITransform? = null
    private val renderer: ShapeRenderer by lazy { IOC.atOrFail("shapeRenderer") }
    private val initialDir = randomDir()
    private val hitCooldown = CoolDown(hitCooldownTime)

    val body: Circle
        get() = Circle(pos.x, pos.y, radius)

    val center: Vector2
        get() = Vector2(pos.x + radius / 2f, pos.y + radius / 2f)

    fun follow(target: ITransform) {
        this.target = target
    }

    private fun dirTo(t: ITransform?): Vector2 {
        if (t == null) return initialDir
        return t.pos.cpy().sub(pos).nor()
    }

    override fun update() {
        if (shouldDestroy) return

        val dir = dirTo(target)
        pos.mulAdd(dir, speed.cpy().scl(Gdx.graphics.deltaTime))

        renderer.managed(ShapeRenderer.ShapeType.Line) {
            it.color = if (!isBoss) Color.WHITE else Color.GOLD
            it.circle(pos.x, pos.y, radius)
        }

        hitCooldown.update()
    }

    fun damage(bullet: Bullet) {
        hp -= bullet.dmg
        if (hp <= 0) {
            shouldDestroy = true
        }
    }

    fun damage(damage: Float) {
        hp -= damage
        if (hp <= 0) {
            shouldDestroy = true
        }
    }

    fun tryHit(func: () -> Unit) {
        hitCooldown.invoke { func() }
    }
}
