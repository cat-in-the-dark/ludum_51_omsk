package org.catinthedark.jvcrplotter.game.states

import org.catinthedark.jvcrplotter.game.entities.EnemiesController
import org.catinthedark.jvcrplotter.game.entities.EnemyGenerator
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.states.IState

class EnemiesTestState : IState {
    private lateinit var controller: EnemiesController
    private val generators = mutableListOf<EnemyGenerator>()

    override fun onActivate() {
        controller = EnemiesController()
        IOC.put("enemiesController", controller)
        generators.add(EnemyGenerator(50f, 50f, 300f, 300f))
    }

    override fun onUpdate() {
        controller.update()
        generators.forEach { it.update() }
    }

    override fun onExit() {
        generators.clear()
    }
}
