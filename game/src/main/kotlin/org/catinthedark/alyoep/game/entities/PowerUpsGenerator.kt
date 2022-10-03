package org.catinthedark.alyoep.game.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import org.catinthedark.alyoep.game.Const
import org.catinthedark.alyoep.game.entities.powerups.FirePowerUp
import org.catinthedark.alyoep.game.entities.powerups.HealPowerUp
import org.catinthedark.alyoep.game.entities.powerups.NovaPowerUp
import org.catinthedark.alyoep.game.entities.powerups.PowerUp
import org.catinthedark.alyoep.lib.IOC
import org.catinthedark.alyoep.lib.RepeatBarrier
import org.catinthedark.alyoep.lib.atOrFail
import kotlin.random.Random

class PowerUpsGenerator {
    private val players: MutableList<Player> by lazy { IOC.atOrFail("players") }
    private val repeater = RepeatBarrier(0f, Const.Balance.PowerUp.TIMEOUT)
    private val controller: PowerUpsController by lazy { IOC.atOrFail("powerUpsController") }

    private val powerUpsPool = listOf("fire", "fire", "fire", "nova", "nova", "heal")

    private var time = 0f
    private var round = 0

    fun update() {
        time += Gdx.graphics.deltaTime
        repeater.invoke {
            val count = players.size
            val pool = if (round < 3) {
                powerUpsPool.filter { it != "heal" }
            } else {
                powerUpsPool
            }

            val powerUps = pool.asSequence().shuffled().take(count).toList()
            round++
            for (i in 0 until count) {
                // TODO: don't spawn powerUp inside tower
                val x = (Random.nextFloat() * Const.Screen.WIDTH).coerceIn(32f, Const.Screen.WIDTH - 32f)
                val y = (Random.nextFloat() * Const.Screen.HEIGHT).coerceIn(32f, Const.Screen.HEIGHT - 32f)
                val powerUp = createPowerUp(powerUps[i], Vector2(x, y))
                controller.addPowerUp(powerUp)
            }
        }
    }

    private fun createPowerUp(name: String, pos: Vector2): PowerUp {
        return when (name) {
            "fire" -> {
                FirePowerUp(pos)
            }

            "nova" -> {
                NovaPowerUp(pos)
            }

            "heal" -> {
                HealPowerUp(pos)
            }

            else -> {
                TODO("Provide the return value")
            }
        }
    }
}
