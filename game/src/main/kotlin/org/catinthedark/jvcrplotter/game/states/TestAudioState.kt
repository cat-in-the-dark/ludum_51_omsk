package org.catinthedark.jvcrplotter.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.Stage
import org.catinthedark.jvcrplotter.game.*
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.atOrFail
import org.catinthedark.jvcrplotter.lib.managed
import org.catinthedark.jvcrplotter.lib.states.IState

class TestAudioState : IState {
    private val hud: Stage by lazy { IOC.atOrFail("hud") }
    private val am: AssetManager by lazy { IOC.atOrFail("assetManager") }

    private var currentTime = 0f

    override fun onActivate() {
        currentTime = 0f

        am.at(Assets.Musics.MAIN_BG_LOOP).apply {
            isLooping = true
            play()
        }
    }

    override fun onUpdate() {
        hud.batch.managed { b ->
            b.draw(am.texture(Assets.Names.TITLE), 0f, 0f)
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            IOC.put("state", States.SPLASH_SCREEN)
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            am.at(Assets.Sounds.POWERUP).play()
        }
    }

    override fun onExit() {
        am.at(Assets.Musics.MAIN_BG_LOOP).stop()
    }
}
