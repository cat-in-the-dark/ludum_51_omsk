package org.catinthedark.jvcrplotter.game.entities

import org.catinthedark.jvcrplotter.game.entities.powerups.PowerUp
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.atOrFail

class PowerUpsController {
    private val powerUps: MutableList<PowerUp> = mutableListOf()
    private val players: List<Player> by lazy { IOC.atOrFail("players") }

    init {
        IOC.put("powerUps", powerUps)
    }

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
