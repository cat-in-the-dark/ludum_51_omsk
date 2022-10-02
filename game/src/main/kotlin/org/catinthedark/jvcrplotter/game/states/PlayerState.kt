package org.catinthedark.jvcrplotter.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import org.catinthedark.jvcrplotter.audio.Bgm
import org.catinthedark.jvcrplotter.game.Assets
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
import java.util.PriorityQueue

class PlayerState : IState {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val colors = listOf(Color.CORAL, Color.CHARTREUSE, Color.GOLDENROD, Color.ROYAL)

    private val controllers = mutableMapOf<PlayerController, Boolean>(
        Pair(PlayerControllerWasd(), false),
        Pair(PlayerControllerArrowKeys(), false)
    )

    private val enemiesController = IOC.atOrPut("enemiesController", EnemiesController())
    private val enemies: List<SimpleEnemy> by lazy { IOC.atOrFail("enemies") }
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

        players.forEachIndexed { idx, player ->
            player.update()
            tryShoot(player, idx)
        }

        enemiesController.update()
        bullets.forEach { it.update() }
        enemyGenerators.forEach { it.update() } // TODO: update only for online players
        powerUpsGenerator.update()
        powerUpsController.update()
        tower.update()
        bgm.update()

        // TODO remove testBgm
        testBgm(Input.Keys.NUM_1, Assets.Music.BASS)
        testBgm(Input.Keys.NUM_2, Assets.Music.LO_TRASH)
        testBgm(Input.Keys.NUM_3, Assets.Music.HI_TRASH)
        testBgm(Input.Keys.NUM_4, Assets.Music.DREAM)
    }

    private fun testBgm(key: Int, music: Assets.Music) {
        if (Gdx.input.isKeyPressed(key)) {
            bgm.fadeIn(music)
        } else {
            bgm.fadeOut(music)
        }
    }

    override fun onExit() {
    }

    private fun tryShoot(player: Player, playerId: Int) {
        val targets = enemies.map { enemy ->
            val dst = player.center.dst(enemy.pos)
            Pair(enemy, dst)
        }.filter { (_, dst) ->
            dst <= Const.Balance.MAX_SHOOT_DIST
        }.sortedBy { it.second }

        if (targets.isEmpty()) {
            return
        }

        // TODO: shoot only if there ia any enemy in a distance
        bgm.tryShootIf(playerId, 1) {
            for (i in 0 until player.stats.bulletsCount) {
                val target = targets[i % targets.size]
                val dir = target.first.pos.cpy().sub(player.center).nor()
                val pos = player.pos.cpy()
                if (i >= targets.size) {
                    val jitter = randomDir().scl(2f)
                    pos.add(jitter)
                    dir.add(jitter).nor()
                }
                bullets.add(Bullet(pos, dir))
            }
        }
    }
}
