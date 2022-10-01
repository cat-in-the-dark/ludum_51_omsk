package org.catinthedark.jvcrplotter.game.entities

import org.catinthedark.jvcrplotter.game.entities.powerups.PowerUp
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.atOrFail
import org.catinthedark.jvcrplotter.lib.atOrPut

class PowerUpsController {
    private val powerUps: MutableList<PowerUp> = IOC.atOrPut("powerUps", mutableListOf())
    private val players: List<Player> by lazy { IOC.atOrFail("players") }

    fun addPowerUp(powerUp: PowerUp) {
        powerUps.add(powerUp)
    }

    fun update() {
        players.forEach { player ->
            val collidedPowerUps = powerUps.filter {
                it.getCollisionRect().overlaps(player.getCollisionRect())
            }.toList()
            collidedPowerUps.forEach {
                it.apply(player)
            }
            powerUps.removeAll(collidedPowerUps)
        }

        powerUps.forEach {
            it.update()
        }
    }
}
