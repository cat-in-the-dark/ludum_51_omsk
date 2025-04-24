package org.catinthedark.alyoep.game.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import org.catinthedark.alyoep.game.Const
import org.catinthedark.alyoep.game.Const.Balance.Spawn.MAX_SPAWN
import org.catinthedark.alyoep.game.Const.Balance.Spawn.SIN_TIME_SCALE
import org.catinthedark.alyoep.lib.IOC
import org.catinthedark.alyoep.lib.RepeatBarrier
import org.catinthedark.alyoep.lib.atOrFail
import org.catinthedark.alyoep.lib.managed
import kotlin.random.Random

class EnemyGenerator(
    private val bounds: Rectangle,
    private val demoMode: Boolean = false
) {
    private val renderer: ShapeRenderer by lazy { IOC.atOrFail("shapeRenderer") }
    private val repeater = RepeatBarrier(0f, Const.Balance.Spawn.TIMEOUT)
    private val controller: EnemiesController by lazy { IOC.atOrFail("enemiesController") }

    private var time: Float = 0f
    private var oldWaveNumber: Int = 0

    var demoWaveNumber: Int = 1

    fun update(currentMaxBossness: Int = 0) {
        time += Gdx.graphics.deltaTime
        repeater.invoke {
            val fixedTime = time / SIN_TIME_SCALE
            val minSpawn = if (demoMode) {
                0.2f
            } else {
                0f
            }
            val maxSpawn = if (demoMode) {
                0.5f
            } else {
                1f
            }
            val spawn = MathUtils.map(-1f, 1f, minSpawn, maxSpawn, MathUtils.sin(fixedTime * (1 + 4 * currentMaxBossness)))

            val waveNumber = if (demoMode) {
                demoWaveNumber
            } else {
                MathUtils.ceil(fixedTime / MathUtils.PI2)
            }

            val count = MathUtils.round(waveNumber * spawn * MAX_SPAWN * (1 shl currentMaxBossness))

            val minSpeed = 50f
            val maxSpeed = minSpeed + waveNumber * 2f

            val minHp = 0.4f + waveNumber * 0.2f
            val maxHp = 0.5f + waveNumber * 0.4f

            val minSize = 20f
            val maxSize = 30f

            val bossSize = 50f
            val bossHp = 5f + waveNumber * 5f

            val bossSpeed = 10f + waveNumber

            val megaBossSize = 100f
            val megaBossHp = 45f + waveNumber * 45f
            val megaBossSpeed = 3f + waveNumber / 3

            val minDamage = 0.5f
            val maxDamage = 1.0f

            for (i in 0 until count) {
                val size = MathUtils.map(0f, 1f, minSize, maxSize, MathUtils.random())
                val hp = MathUtils.map(minSize, maxSize, minHp, maxHp, size)
                val speed = MathUtils.map(maxSize, minSize, minSpeed, maxSpeed, size)
                val damage = MathUtils.map(minSize, maxSize, minDamage, maxDamage, size)

                spawnEnemy(size, hp, speed, damage, 0)
            }

            if (oldWaveNumber != waveNumber && waveNumber > 3) {
                if (waveNumber % 9 == 0 || waveNumber > 25 && waveNumber % 5 == 0) {
                    val damage = 4 * MathUtils.map(0f, 1f, minDamage, maxDamage, MathUtils.random())
                    spawnEnemy(megaBossSize, megaBossHp, megaBossSpeed, damage, 2)
                } else if (waveNumber % 2 == 0) {
                    val damage = 2 * MathUtils.map(0f, 1f, minDamage, maxDamage, MathUtils.random())
                    spawnEnemy(bossSize, bossHp, bossSpeed, damage, 1)
                }
            }

            oldWaveNumber = waveNumber
        }

        renderer.managed(ShapeRenderer.ShapeType.Line) {
            // DEBUG!
            it.color = Color.WHITE;
            it.rect(bounds.x, bounds.y, bounds.width, bounds.height)
        }
    }

    fun spawnEnemy(size: Float, hp: Float, speed: Float, damage: Float, bossness: Int) {
        val x = bounds.x + Random.nextFloat() * bounds.width
        val y = bounds.y + Random.nextFloat() * bounds.height

        val enemy = SimpleEnemy(
            Vector2(x, y),
            radius = size,
            damage = damage,
            hitCooldownTime = 0.1f,
            hp = hp,
            bossness = bossness,
            speed = Vector2(speed, speed)
        )
        controller.registerEnemy(enemy)
    }
}
