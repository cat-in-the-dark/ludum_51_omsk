package org.catinthedark.jvcrplotter.game.entities

import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Vector2
import org.catinthedark.jvcrplotter.game.entities.powerups.PowerUp
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.atOrFail
import kotlin.math.pow

fun intersectCircles(center1: Vector2, radius1: Float, center2: Vector2, radius2: Float): Boolean {
    val dist = center1.dst(center2)
    val sumR = radius1 + radius2
    return dist <= sumR
}


class CollisionsSystem {
    fun update() {
        val enemies: MutableList<SimpleEnemy> = IOC.atOrFail("enemies")
        val players: MutableList<Player> = IOC.atOrFail("players")
        val bullets: MutableList<Bullet> = IOC.atOrFail("bullets")
        val powerUps: MutableList<PowerUp> = IOC.atOrFail("powerUps")

        players.forEach { player ->
            // TODO: collide them
            val collidedPowerUps = powerUps.filter {
                it.getCollisionRect().overlaps(player.getCollisionRect())
            }.toList()
            collidedPowerUps.forEach {
                it.apply(player)
            }
            powerUps.removeAll(collidedPowerUps)
        }

        enemies.forEach { enemy ->
            players.forEach { player ->
                if (intersectCircles(
                        enemy.pos,
                        enemy.radius,
                        player.pos,
                        player.exradius
                    )
                ) {
                    onHitPlayerEnemy(player, enemy)
                }
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

    private fun onHitPlayerEnemy(player: Player, enemy: SimpleEnemy) {
        enemy.tryHitPlayer(player) {
            player.hit(enemy.damage)
        }
    }
}
