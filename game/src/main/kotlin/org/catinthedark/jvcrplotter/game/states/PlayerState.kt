package org.catinthedark.jvcrplotter.game.states

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import org.catinthedark.jvcrplotter.game.control.PlayerControllerArrowKeys
import org.catinthedark.jvcrplotter.game.control.PlayerControllerWasd
import org.catinthedark.jvcrplotter.game.entities.Player
import org.catinthedark.jvcrplotter.lib.states.IState
import org.slf4j.LoggerFactory

class PlayerState : IState {
    private val logger = LoggerFactory.getLogger(javaClass)

    private lateinit var playerA: Player
    private lateinit var playerB: Player

    override fun onActivate() {
        logger.info("here!")
        playerA = Player(Vector2(0f, 0f), Color.GREEN, PlayerControllerWasd())
        playerB = Player(Vector2(0f, 0f), Color.BLUE, PlayerControllerArrowKeys())
    }

    override fun onUpdate() {
        playerA.update()
        playerB.update()
    }

    override fun onExit() {
    }
}
