package org.catinthedark.jvcrplotter.game.entities

import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.atOrFail

class CollisionsSystem {
    fun update() {
        val enemies: List<SimpleEnemy> = IOC.atOrFail("enemies")
        val players: List<Player> = IOC.atOrFail("players")
        val bullets: List<Bullet> = IOC.atOrFail("bullets")
    }
}
