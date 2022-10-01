package org.catinthedark.jvcrplotter.game.states

import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import org.catinthedark.jvcrplotter.game.Const
import org.catinthedark.jvcrplotter.game.control.PlayerController
import org.catinthedark.jvcrplotter.game.control.PlayerControllerArrowKeys
import org.catinthedark.jvcrplotter.game.control.PlayerControllerGamepad
import org.catinthedark.jvcrplotter.game.control.PlayerControllerWasd
import org.catinthedark.jvcrplotter.game.entities.Bullet
import org.catinthedark.jvcrplotter.game.entities.EnemiesController
import org.catinthedark.jvcrplotter.game.entities.EnemyGenerator
import org.catinthedark.jvcrplotter.game.entities.Player
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.RepeatBarrier
import org.catinthedark.jvcrplotter.lib.math.randomDir
import org.catinthedark.jvcrplotter.lib.states.IState
import org.slf4j.LoggerFactory

class PlayerState : IState {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val colors = listOf(Color.CORAL, Color.CHARTREUSE, Color.GOLDENROD, Color.ROYAL)

    private val controllers = mutableMapOf<PlayerController, Boolean>(
        Pair(PlayerControllerWasd(), false),
        Pair(PlayerControllerArrowKeys(), false)
    )

    private val enemiesController = EnemiesController()
    private val enemyGenerators = listOf(
        EnemyGenerator(Const.Balance.generatorPlaces[0]),
        EnemyGenerator(Const.Balance.generatorPlaces[1]),
        EnemyGenerator(Const.Balance.generatorPlaces[2]),
        EnemyGenerator(Const.Balance.generatorPlaces[3]),
    )
    private val bullets = mutableListOf<Bullet>()
    private val players: MutableList<Player> = mutableListOf()
    private val gamepads: Array<Controller>? = Controllers.getControllers()

    init {
        IOC.put("players", players)
        IOC.put("enemiesController", enemiesController)
    }

    private val cooldown = RepeatBarrier(0.5f)
    private fun spawnBullets() {
        cooldown.invoke {
            players.forEach { player ->
                // TODO: find closest enemy
                val dir = randomDir()
                bullets.add(Bullet(player.pos.cpy(), dir))
            }
        }
    }

    override fun onActivate() {
        logger.info("here!")
        gamepads?.forEach {
            logger.info("Gamepad: ${it.name} ${it.uniqueId}")
            if (controllers.size >= Const.Balance.MAX_PLAYERS) {
                return@forEach
            }

            controllers[PlayerControllerGamepad(it)] = false
        }
    }

    override fun onUpdate() {
        controllers.forEach {
            if (!it.value) {
                if (it.key.getDirection().len() > 0.0001) {
                    players.add(Player(Vector2(0f, 0f), colors[players.size], it.key))
                    controllers[it.key] = true
                }
            }
        }

        players.forEach {
            it.update()
        }

        enemiesController.update()
        spawnBullets()
        bullets.forEach { it.update() }
        enemyGenerators.forEach { it.update() } // TODO: update only for online players
    }

    override fun onExit() {
    }
}
