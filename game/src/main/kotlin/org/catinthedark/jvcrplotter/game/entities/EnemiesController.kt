package org.catinthedark.jvcrplotter.game.entities

import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.atOrFail
import org.catinthedark.jvcrplotter.lib.math.findClosest

class EnemiesController {
    private val enemies: MutableList<SimpleEnemy> = mutableListOf()
    private val players: List<Player> = IOC.atOrFail("players")

    init {
        IOC.put("enemies", enemies)
    }

    fun registerEnemy(enemy: SimpleEnemy) {
        enemies.add(enemy)
    }

    fun update() {
        // TODO: get attractor
        enemies.forEach {
            findClosest(it, players)?.apply {
                it.follow(this)
            }
            it.update()
        }
    }
}
