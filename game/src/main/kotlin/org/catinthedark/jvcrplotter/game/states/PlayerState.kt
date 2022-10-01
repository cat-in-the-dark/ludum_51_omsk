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
import org.catinthedark.jvcrplotter.game.entities.Player
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.atOr
import org.catinthedark.jvcrplotter.lib.states.IState
import org.slf4j.LoggerFactory

class PlayerState : IState {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val colors = listOf(Color.CORAL, Color.CHARTREUSE, Color.GOLDENROD, Color.ROYAL)

    private val controllers = mutableMapOf<PlayerController, Boolean>(
        Pair(PlayerControllerWasd(), false),
        Pair(PlayerControllerArrowKeys(), false)
    )

    private val players: MutableList<Player> by lazy { IOC.atOr("players", mutableListOf()) }
    private val gamepads: Array<Controller>? = Controllers.getControllers()

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
    }

    override fun onExit() {
    }
}
