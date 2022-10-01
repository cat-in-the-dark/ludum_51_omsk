package org.catinthedark.jvcrplotter.game.states

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import org.catinthedark.jvcrplotter.game.Const
import org.catinthedark.jvcrplotter.game.control.PlayerController
import org.catinthedark.jvcrplotter.game.control.PlayerControllerArrowKeys
import org.catinthedark.jvcrplotter.game.control.PlayerControllerWasd
import org.catinthedark.jvcrplotter.game.entities.EnemiesController
import org.catinthedark.jvcrplotter.game.entities.EnemyGenerator
import org.catinthedark.jvcrplotter.game.entities.Player
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.states.IState

class EnemiesTestState : IState {
    private lateinit var controller: EnemiesController
    private val players = listOf(
        Player(Vector2(640f, 360f), Color.GREEN, PlayerControllerWasd()),
        Player(Vector2(80f, 150f), Color.GREEN, PlayerControllerArrowKeys()),
    )
    private val generators = mutableListOf<EnemyGenerator>()

    override fun onActivate() {
        IOC.put("players", players)
        controller = EnemiesController()
        IOC.put("enemiesController", controller)
        generators.add(EnemyGenerator(Const.Balance.generatorPlaces[0]))
        generators.add(EnemyGenerator(Const.Balance.generatorPlaces[1]))
        generators.add(EnemyGenerator(Const.Balance.generatorPlaces[2]))
        generators.add(EnemyGenerator(Const.Balance.generatorPlaces[3]))
    }

    override fun onUpdate() {
        controller.update()
        players.forEach { it.update() }
        generators.forEach { it.update() }
    }

    override fun onExit() {
        generators.clear()
    }
}
