package org.catinthedark.jvcrplotter.game.entities

class EnemiesController {
    private val enemies: MutableList<SimpleEnemy> = mutableListOf()

    private fun findTarget() {

    }

    fun registerEnemy(enemy: SimpleEnemy) {
        enemies.add(enemy)
    }

    fun update() {
        // TODO: get attractor
        enemies.forEach {
            it.update()
        }
    }
}
