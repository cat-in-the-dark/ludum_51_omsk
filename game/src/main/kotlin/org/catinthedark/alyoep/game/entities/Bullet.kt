package org.catinthedark.alyoep.game.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import org.catinthedark.alyoep.lib.IOC
import org.catinthedark.alyoep.lib.atOrFail
import org.catinthedark.alyoep.lib.interfaces.ICollisionRect
import org.catinthedark.alyoep.lib.interfaces.IDestructible
import org.catinthedark.alyoep.lib.interfaces.ITransform
import org.catinthedark.alyoep.lib.interfaces.IUpdatable
import org.catinthedark.alyoep.lib.managed
import org.catinthedark.alyoep.lib.math.isInViewPort

class Bullet(
    override val pos: Vector2,
    private val dir: Vector2,
    val dmg: Float,
    private val speed: Vector2 = Vector2(350f, 350f),
) : ITransform, IUpdatable, IDestructible, ICollisionRect {
    override var shouldDestroy = false
    private val size = 25f
    private val renderer: ShapeRenderer by lazy { IOC.atOrFail("shapeRenderer") }
    val posEnd: Vector2
        get() = Vector2(pos.x + dir.x * size, pos.y + dir.y * size)

    override fun update() {
        pos.mulAdd(dir, speed.cpy().scl(Gdx.graphics.deltaTime))

        renderer.managed(ShapeRenderer.ShapeType.Line) {
            it.color = Color.RED
            it.line(pos, posEnd)
        }

        if (!isInViewPort(this)) {
            shouldDestroy = true
        }
    }

    fun damage(enemy: SimpleEnemy) {
        // TODO: check if bullet should be destroyed
        shouldDestroy = true
    }

    override fun getCollisionRect(): Rectangle {
        return Rectangle(pos.x, pos.y, dir.x * size, dir.y * size)
    }
}
