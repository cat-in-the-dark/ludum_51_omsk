package org.catinthedark.alyoep.game.entities

import org.catinthedark.alyoep.game.Const
import org.catinthedark.alyoep.lib.IOC
import org.catinthedark.alyoep.lib.atOrFail
import org.catinthedark.alyoep.lib.atOrPut
import org.catinthedark.alyoep.lib.math.findClosest

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
