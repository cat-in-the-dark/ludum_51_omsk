package org.catinthedark.jvcrplotter.game.entities

import org.catinthedark.jvcrplotter.game.Const
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.atOrFail
import org.catinthedark.jvcrplotter.lib.atOrPut
import org.catinthedark.jvcrplotter.lib.math.findClosest

class EnemiesController {
    private val enemies: MutableList<SimpleEnemy> = IOC.atOrPut("enemies", mutableListOf())
    private val players: List<Player> by lazy { IOC.atOrFail("players") }
    private val tower: Tower by lazy { IOC.atOrFail("tower") }

    fun registerEnemy(enemy: SimpleEnemy) {
        enemies.add(enemy)
    }

    fun update() {
        // TODO: get attractor
        enemies.forEach { enemy ->
            val closestPlayer = findClosest(enemy, players)
            if (closestPlayer != null && closestPlayer.second < Const.Balance.MAX_FOLLOW_DIST) {
                enemy.follow(closestPlayer.first)
            } else {
                enemy.follow(tower)
            }
            enemy.update()
        }
    }
}
