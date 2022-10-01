package org.catinthedark.jvcrplotter.game.states

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.atOrFail
import org.catinthedark.jvcrplotter.lib.managed
import org.catinthedark.jvcrplotter.lib.states.IState
import org.slf4j.LoggerFactory

class PlayerState : IState {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val renderer: ShapeRenderer by lazy { IOC.atOrFail("shapeRenderer") }
    private val stage: Stage by lazy { IOC.atOrFail<Stage>("stage") }

    override fun onActivate() {
        logger.info("here!")
    }

    override fun onUpdate() {
        stage.batch.managed {
            renderer.managed(ShapeRenderer.ShapeType.Filled) {
                it.color = Color.CORAL
                it.circle(50.0f, 50.0f, 10.0f)
                it.color = Color.BLUE
                it.circle(250.0f, 550.0f, 10.0f)
            }
        }
    }

    override fun onExit() {
    }
}
