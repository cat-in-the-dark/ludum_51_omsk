package org.catinthedark.alyoep.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.Stage
import org.catinthedark.alyoep.game.Assets
import org.catinthedark.alyoep.game.States
import org.catinthedark.alyoep.game.control.PlayerController
import org.catinthedark.alyoep.game.texture
import org.catinthedark.alyoep.lib.IOC
import org.catinthedark.alyoep.lib.atOrFail
import org.catinthedark.alyoep.lib.managed
import org.catinthedark.alyoep.lib.states.IState

class GameOverState : IState {
    private val hud: Stage by lazy { IOC.atOrFail<Stage>("hud") }
    private val am: AssetManager by lazy { IOC.atOrFail<AssetManager>("assetManager") }
    private val controllers: Map<PlayerController, Boolean> = IOC.atOrFail("input")

    override fun onActivate() {
    }

    override fun onUpdate() {
        hud.batch.managed { b ->
            b.draw(am.texture(Assets.Names.GAME_OVR), 0f, 0f)
        }

        for (controller in controllers) {
            if (controller.value && controller.key.isStartPressed()) {
                IOC.put("state", States.TITLE_SCREEN)
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            IOC.put("state", States.TITLE_SCREEN)
        }
    }

    override fun onExit() {
    }
}
