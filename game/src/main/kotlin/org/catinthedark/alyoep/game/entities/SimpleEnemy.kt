package org.catinthedark.alyoep.game.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.MathUtils.sin
import com.badlogic.gdx.math.Vector2
import org.catinthedark.alyoep.audio.Bgm
import org.catinthedark.alyoep.lib.CoolDown
import org.catinthedark.alyoep.lib.IOC
import org.catinthedark.alyoep.lib.atOrFail
import org.catinthedark.alyoep.lib.interfaces.IDestructible
import org.catinthedark.alyoep.lib.interfaces.ITransform
import org.catinthedark.alyoep.lib.interfaces.IUpdatable
import org.catinthedark.alyoep.lib.managed
import org.catinthedark.alyoep.lib.math.randomDir

class SimpleEnemy(
    override val pos: Vector2,
    val radius: Float,
    val damage: Float,
    private var hp: Float,
    val speed: Vector2,
    val bossness: Int,
    private val hitCooldownTime: Float
) : ITransform, IUpdatable, IDestructible {
    override var shouldDestroy = false
    private var target: ITransform? = null
    private val renderer: ShapeRenderer by lazy { IOC.atOrFail("shapeRenderer") }
    private val initialDir = randomDir()
    private val hitCooldown = CoolDown(hitCooldownTime)
    private val bgm: Bgm by lazy { IOC.atOrFail("bgm") }

    private var drawDamage = false
    private var drawDamageTime = 0f

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

        if (drawDamage) {
            drawDamageTime += Gdx.graphics.deltaTime
            Gdx.gl.glEnable(GL20.GL_BLEND)
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

            renderer.managed(ShapeRenderer.ShapeType.Filled) {
                val clr = Color(Color.RED)
                val arg = drawDamageTime * MathUtils.PI * 4
                clr.a = sin(arg) * 0.5f
                it.color = clr
                it.circle(pos.x, pos.y, radius)
                if (arg > MathUtils.PI) {
                    drawDamage = false
                }
            }
            Gdx.gl.glDisable(GL20.GL_BLEND)
        } else {
            drawDamageTime = 0f
        }

        renderer.managed(ShapeRenderer.ShapeType.Line) {
            it.color = when (bossness) {
                0 -> Color.WHITE
                1 -> Color.GOLD
                else -> Color.DARK_GRAY
            }
            it.circle(pos.x, pos.y, radius)
        }

        hitCooldown.update()
    }

    fun damage(bullet: Bullet) {
        drawDamage = true
        hp -= bullet.dmg
        if (hp <= 0) {
            shouldDestroy = true
        }
    }

    fun damage(damage: Float) {
        drawDamage = true
        hp -= damage
        if (hp <= 0) {
            shouldDestroy = true
        }
    }

    fun tryHitPlayer(playerIdx: Int, func: () -> Unit) {
        bgm.playerHits.tryShootIf(playerIdx, 3, func)
    }

    fun tryHitTower(func: () -> Unit) {
        bgm.towerHits.tryShootIf(0, 3, func)
    }
}
