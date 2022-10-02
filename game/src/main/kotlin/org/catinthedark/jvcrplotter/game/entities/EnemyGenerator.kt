package org.catinthedark.jvcrplotter.game.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import org.catinthedark.jvcrplotter.game.Const
import org.catinthedark.jvcrplotter.game.Const.Balance.Spawn.MAX_SPAWN
import org.catinthedark.jvcrplotter.game.Const.Balance.Spawn.SIN_TIME_SCALE
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.RepeatBarrier
import org.catinthedark.jvcrplotter.lib.atOrFail
import org.catinthedark.jvcrplotter.lib.managed
import org.slf4j.LoggerFactory
import kotlin.random.Random

class EnemyGenerator(
    private val bounds: Rectangle
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val renderer: ShapeRenderer by lazy { IOC.atOrFail("shapeRenderer") }
    private val repeater = RepeatBarrier(0f, Const.Balance.Spawn.TIMEOUT)
    private val controller: EnemiesController by lazy { IOC.atOrFail("enemiesController") }

    private var time: Float = 0f

    fun update() {
        time += Gdx.graphics.deltaTime
        repeater.invoke {
            val s = (MathUtils.sin(time / SIN_TIME_SCALE) + 1) / 2f // 0..1
            val count = MathUtils.round(MAX_SPAWN * s)
            for (i in 0..count) {
                val x = bounds.x + Random.nextFloat() * bounds.width
                val y = bounds.y + Random.nextFloat() * bounds.height
                val enemy = SimpleEnemy(
                    Vector2(x, y),
                    radius = 20f,
                    damage = 0.5f,
                    hitCooldownTime = 0.1f,
                    hp = 1f,
                )
                controller.registerEnemy(enemy)
            }
        }

        renderer.managed(ShapeRenderer.ShapeType.Line) {
            // DEBUG!
            it.color = Color.WHITE;
            it.rect(bounds.x, bounds.y, bounds.width, bounds.height)
        }
    }
}
