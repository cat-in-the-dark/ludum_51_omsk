package org.catinthedark.jvcrplotter.game.entities

import com.badlogic.gdx.math.Vector2
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.atOrFail

class EnemiesController {
    private val enemies: MutableList<SimpleEnemy> = mutableListOf()
    private val players: List<Player> = IOC.atOrFail("players")

    private fun findTarget(from: Vector2): Vector2? {
        var minDist = Float.MAX_VALUE
        var target: Vector2? = null
        players.forEach {
            val dst = it.pos.dst2(from)
            if (dst < minDist) {
                minDist = dst
                target = it.pos
            }
        }
        return target
    }

    fun registerEnemy(enemy: SimpleEnemy) {
        enemies.add(enemy)
    }

    fun update() {
        // TODO: get attractor
        enemies.forEach {
            findTarget(it.pos)?.apply {
                it.follow(this)
            }
            it.update()
        }
    }
}
