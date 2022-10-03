package org.catinthedark.alyoep.game.entities

import org.catinthedark.alyoep.game.entities.powerups.PowerUp
import org.catinthedark.alyoep.lib.IOC
import org.catinthedark.alyoep.lib.atOrFail
import org.catinthedark.alyoep.lib.atOrPut

class PowerUpsController {
    private val powerUps: MutableList<PowerUp> = IOC.atOrPut("powerUps", mutableListOf())
    private val players: List<Player> by lazy { IOC.atOrFail("players") }

    fun addPowerUp(powerUp: PowerUp) {
        powerUps.add(powerUp)
    }

    fun update() {
        powerUps.forEach {
            it.update()
        }
    }
}
