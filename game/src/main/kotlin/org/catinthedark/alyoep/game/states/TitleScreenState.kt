package org.catinthedark.alyoep.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.Stage
import org.catinthedark.alyoep.audio.Bgm
import org.catinthedark.alyoep.game.Assets
import org.catinthedark.alyoep.game.States
import org.catinthedark.alyoep.game.control.PlayerController
import org.catinthedark.alyoep.game.texture
import org.catinthedark.alyoep.lib.IOC
import org.catinthedark.alyoep.lib.atOrFail
import org.catinthedark.alyoep.lib.managed
import org.catinthedark.alyoep.lib.states.IState

class TitleScreenState : IState {
    private val hud: Stage by lazy { IOC.atOrFail<Stage>("hud") }
    private val am: AssetManager by lazy { IOC.atOrFail<AssetManager>("assetManager") }
    private val bgm: Bgm by lazy { IOC.atOrFail("bgm") }
    private val controllers: Map<PlayerController, Boolean> = IOC.atOrFail("input")

    private var currentTime = 0f

    override fun onActivate() {
        currentTime = 0f
    }

    override fun onUpdate() {
        bgm.update()
        hud.batch.managed { b ->
            b.draw(am.texture(Assets.Names.TITLE), 0f, 0f)
        }

        var startPressed = false
        for (controller in controllers) {
            if (controller.key.isStartJustPressed()) {
                startPressed = true
            }
        }
//        if (!startPressed) {
//            if (Gdx.input.isKeyPressed(Input.Keys.SPACE) ||
//                Gdx.input.isKeyPressed(Input.Keys.ENTER) ||
//                Gdx.input.isKeyPressed(Input.Keys.P)
//            ) {
//                startPressed = true
//            }
//        }

        if (startPressed) {
            if (IOC.atOrFail("showTutorial")) {
                IOC.put("state", States.TUTORIAL_SCREEN)
            } else {
                IOC.put("state", States.PLAYER_SCREEN)
            }
        }
    }

    override fun onExit() {
    }
}
