package org.catinthedark.jvcrplotter.game.entities

import com.badlogic.gdx.math.Vector2

class EnemiesController {
    private val enemies: MutableList<SimpleEnemy> = mutableListOf()

    private val target = Vector2(640f, 360f)

    fun registerEnemy(enemy: SimpleEnemy) {
        enemies.add(enemy)
    }

    fun update() {
        // TODO: get attractor
        enemies.forEach {
            it.follow(target)
            it.update()
        }
    }
}
