package org.catinthedark.jvcrplotter.game.states

import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import org.catinthedark.jvcrplotter.audio.Bgm
import org.catinthedark.jvcrplotter.game.Const
import org.catinthedark.jvcrplotter.game.control.PlayerController
import org.catinthedark.jvcrplotter.game.control.PlayerControllerArrowKeys
import org.catinthedark.jvcrplotter.game.control.PlayerControllerGamepad
import org.catinthedark.jvcrplotter.game.control.PlayerControllerWasd
import org.catinthedark.jvcrplotter.game.entities.*
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.atOrFail
import org.catinthedark.jvcrplotter.lib.atOrPut
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

    private val enemiesController = IOC.atOrPut("enemiesController", EnemiesController())
    private val powerUpsController = IOC.atOrPut("powerUpsController", PowerUpsController())
    private val enemyGenerators = listOf(
        EnemyGenerator(Const.Balance.generatorPlaces[0]),
        EnemyGenerator(Const.Balance.generatorPlaces[1]),
        EnemyGenerator(Const.Balance.generatorPlaces[2]),
        EnemyGenerator(Const.Balance.generatorPlaces[3]),
    )
    private val bullets: MutableList<Bullet> = IOC.atOrPut("bullets", mutableListOf())
    private val players: MutableList<Player> = IOC.atOrPut("players", mutableListOf())
    private val gamepads: Array<Controller>? = Controllers.getControllers()
    private val collisionsSystem = CollisionsSystem()
    private val garbageCollectorSystem = GarbageCollectorSystem()
    private val bgm: Bgm by lazy { IOC.atOrFail("bgm") }
    private val powerUpsGenerator = PowerUpsGenerator()
    private val tower = Tower(Vector2(Const.Screen.WIDTH / 2f, Const.Screen.HEIGHT / 3f * 2f))

    private fun spawnBullets() {
        players.forEachIndexed { playerId, player ->
            // TODO: shoot only if there ia any enemy in a distance
            bgm.tryShootIf(playerId, 1) {
                for (i in 0 until player.stats.bulletsCount) {
                    val dir = randomDir()
                    bullets.add(Bullet(player.pos.cpy(), dir))
                }
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

        collisionsSystem.update()
        garbageCollectorSystem.update()

        players.forEach {
            it.update()
        }

        enemiesController.update()
        spawnBullets()
        bullets.forEach { it.update() }
        enemyGenerators.forEach { it.update() } // TODO: update only for online players
        powerUpsGenerator.update()
        powerUpsController.update()
        tower.update()
    }

    override fun onExit() {
    }
}
