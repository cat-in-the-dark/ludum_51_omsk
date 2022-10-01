package org.catinthedark.jvcrplotter.game.entities

import org.catinthedark.jvcrplotter.game.Const
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.atOrFail
import org.catinthedark.jvcrplotter.lib.atOrPut
import org.catinthedark.jvcrplotter.lib.math.findClosest

class EnemiesController {
    private val enemies: MutableList<SimpleEnemy> = IOC.atOrPut("enemies", mutableListOf())
    private val players: List<Player> by lazy { IOC.atOrFail("players") }

    fun registerEnemy(enemy: SimpleEnemy) {
        enemies.add(enemy)
    }

    fun update() {
        // TODO: get attractor
        enemies.forEach { enemy ->
            findClosest(enemy, players)?.let {
                if (it.second < Const.Balance.MAX_FOLLOW_DIST) {
                    enemy.follow(it.first)
                }
            }
            enemy.update()
        }
    }
}
