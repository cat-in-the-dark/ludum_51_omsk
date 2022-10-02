package org.catinthedark.jvcrplotter.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import org.catinthedark.jvcrplotter.game.States
import org.catinthedark.jvcrplotter.game.control.PlayerController
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.atOrFail
import org.catinthedark.jvcrplotter.lib.managed
import org.catinthedark.jvcrplotter.lib.states.IState

class GameOverState : IState {
    private val render: ShapeRenderer by lazy { IOC.atOrFail("shapeRenderer") }
    private val am: AssetManager by lazy { IOC.atOrFail<AssetManager>("assetManager") }
    private val controllers: Map<PlayerController, Boolean> = IOC.atOrFail("input")

    override fun onActivate() {
    }

    override fun onUpdate() {
        val startPos = Vector2(30f, 30f)
        render.managed(ShapeRenderer.ShapeType.Line) {
            it.rect(0f, 0f, 300f, 500f)
        }

        for (controller in controllers) {
            if (controller.value && controller.key.isStartPressed()) {
                IOC.put("state", States.PLAYER_SCREEN)
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            IOC.put("state", States.PLAYER_SCREEN)
        }
    }

    override fun onExit() {
    }
}
