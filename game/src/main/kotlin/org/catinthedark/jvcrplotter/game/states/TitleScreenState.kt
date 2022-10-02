package org.catinthedark.jvcrplotter.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.Stage
import org.catinthedark.jvcrplotter.audio.Bgm
import org.catinthedark.jvcrplotter.game.Assets
import org.catinthedark.jvcrplotter.game.States
import org.catinthedark.jvcrplotter.game.control.PlayerController
import org.catinthedark.jvcrplotter.game.texture
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.atOrFail
import org.catinthedark.jvcrplotter.lib.managed
import org.catinthedark.jvcrplotter.lib.states.IState

class TitleScreenState : IState {
    private val hud: Stage by lazy { IOC.atOrFail<Stage>("hud") }
    private val am: AssetManager by lazy { IOC.atOrFail<AssetManager>("assetManager") }
    private val bgm: Bgm by lazy { IOC.atOrFail("bgm") }
    private val controllers: Map<PlayerController, Boolean> = IOC.atOrFail("input")

    private var currentTime = 0f

    override fun onActivate() {
        currentTime = 0f
        IOC.put("showTutorial", true)
    }

    override fun onUpdate() {
        bgm.update()
        hud.batch.managed { b ->
            b.draw(am.texture(Assets.Names.TITLE), 0f, 0f)
        }

        for (controller in controllers) {
            if (controller.key.isStartPressed()) {
                IOC.put("state", States.PLAYER_SCREEN)
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) ||
            Gdx.input.isKeyPressed(Input.Keys.ENTER) ||
            Gdx.input.isKeyPressed(Input.Keys.P)
        ) {
            IOC.put("state", States.PLAYER_SCREEN)
        }
    }

    override fun onExit() {
    }
}
