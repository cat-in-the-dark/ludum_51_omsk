package org.catinthedark.jvcrplotter.game.entities

import com.badlogic.gdx.math.Intersector
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.atOrFail
import kotlin.math.pow

class CollisionsSystem {
    fun update() {
        val enemies: MutableList<SimpleEnemy> = IOC.atOrFail("enemies")
        val players: MutableList<Player> = IOC.atOrFail("players")
        val bullets: MutableList<Bullet> = IOC.atOrFail("bullets")

        enemies.forEach { enemy ->
            players.forEach { player ->
                // TODO: collide them
            }

            bullets.forEach { bullet ->
                // TODO: collide them
                val res = Intersector.intersectSegmentCircle(
                    bullet.pos,
                    bullet.posEnd,
                    enemy.pos,
                    enemy.radius.pow(2)
                )
                if (res) {
                    enemy.damage(bullet)
                    bullet.damage(enemy)
                }
            }
        }
    }
}
