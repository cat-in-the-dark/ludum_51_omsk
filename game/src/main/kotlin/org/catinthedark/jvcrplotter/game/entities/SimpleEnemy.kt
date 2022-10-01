package org.catinthedark.jvcrplotter.game.entities

import com.badlogic.gdx.scenes.scene2d.Stage
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.atOrFail
import org.catinthedark.jvcrplotter.lib.managed

class SimpleEnemy(
    private var posX: Float,
    private var posY: Float
) {
    private val stage: Stage by lazy { IOC.atOrFail("stage") }

    fun update() {
        // TODO: draw enemy view

    }
}
