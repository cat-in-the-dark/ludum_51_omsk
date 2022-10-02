package org.catinthedark.jvcrplotter.game.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import org.catinthedark.jvcrplotter.game.Const
import org.catinthedark.jvcrplotter.game.entities.powerups.FirePowerUp
import org.catinthedark.jvcrplotter.game.entities.powerups.HealPowerUp
import org.catinthedark.jvcrplotter.game.entities.powerups.NovaPowerUp
import org.catinthedark.jvcrplotter.game.entities.powerups.PowerUp
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.RepeatBarrier
import org.catinthedark.jvcrplotter.lib.atOrFail
import kotlin.random.Random

class PowerUpsGenerator {
    private val players: MutableList<Player> by lazy { IOC.atOrFail("players") }
    private val repeater = RepeatBarrier(0f, Const.Balance.PowerUp.TIMEOUT)
    private val controller: PowerUpsController by lazy { IOC.atOrFail("powerUpsController") }

    private val powerUpsPool = listOf("fire", "nova", "heal", "fire")

    private var time = 0f

    fun update() {
        time += Gdx.graphics.deltaTime
        repeater.invoke {
            val count = players.size
            val powerUps = powerUpsPool.asSequence().shuffled().take(count).toList()
            for (i in 0 until count) {
                // TODO: don't spawn powerUp inside tower
                val x = Random.nextFloat() * Const.Screen.WIDTH
                val y = Random.nextFloat() * Const.Screen.HEIGHT
                val powerUp = createPowerUp(powerUps[i], Vector2(x, y))
                controller.addPowerUp(powerUp)
            }
        }
    }

    private fun createPowerUp(name: String, pos: Vector2): PowerUp {
        if (name == "fire") {
            return FirePowerUp(pos)
        }
        if (name == "nova") {
            return NovaPowerUp(pos)
        }
        if (name == "heal") {
            return HealPowerUp(pos)
        }
        return TODO("Provide the return value")
    }
}
