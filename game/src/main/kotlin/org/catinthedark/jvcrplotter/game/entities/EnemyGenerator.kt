package org.catinthedark.jvcrplotter.game.entities

import org.catinthedark.jvcrplotter.game.Const
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.RepeatBarrier
import org.catinthedark.jvcrplotter.lib.atOrFail
import org.slf4j.LoggerFactory
import kotlin.random.Random

class EnemyGenerator(
    private val posX: Float,
    private val posY: Float,
    private val width: Float,
    private val height: Float
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val repeater = RepeatBarrier(Const.Balance.ENEMY_SPAWN_TIMEOUT)
    private val controller: EnemiesController by lazy { IOC.atOrFail("enemiesController") }

    fun update() {
        repeater.invoke {
            logger.info("Spawn Enemy")
            val x = posX + Random.nextFloat() * width
            val y = posY + Random.nextFloat() * height
            val enemy = SimpleEnemy(posX = x, posY = y)
            controller.registerEnemy(enemy)
        }


    }
}
