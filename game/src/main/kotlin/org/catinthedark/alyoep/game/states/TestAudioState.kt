package org.catinthedark.alyoep.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.Stage
import org.catinthedark.alyoep.audio.Bgm
import org.catinthedark.alyoep.game.*
import org.catinthedark.alyoep.lib.IOC
import org.catinthedark.alyoep.lib.atOrFail
import org.catinthedark.alyoep.lib.managed
import org.catinthedark.alyoep.lib.states.IState

class TestAudioState : IState {
    private val hud: Stage by lazy { IOC.atOrFail("hud") }
    private val am: AssetManager by lazy { IOC.atOrFail("assetManager") }
    private val bgm: Bgm by lazy { IOC.atOrFail("bgm") }

    private var currentTime = 0f

    private var bgmActiveLayers = 0

    override fun onActivate() {
        currentTime = 0f

        bgm.fadeInAll()
    }

    override fun onUpdate() {
        bgm.update()

        hud.batch.managed { b ->
            b.draw(am.texture(Assets.Names.TITLE), 110f, 110f)
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            IOC.put("state", States.SPLASH_SCREEN)
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            am.at(Assets.Sounds.POWERUP).play()
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            am.at(Assets.Sounds.POWERUP).play()
        }

        if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) bgm.novas.tryShoot(0, 1)
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) bgm.novas.tryShoot(0, 2)
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) bgm.novas.tryShoot(0, 3)
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_4)) bgm.novas.tryShoot(0, 4)
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_5)) bgm.novas.tryShoot(1, 1)
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_6)) bgm.novas.tryShoot(1, 2)
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_7)) bgm.novas.tryShoot(1, 3)
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_8)) bgm.novas.tryShoot(1, 4)
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_9)) bgm.novas.tryShoot(2, 1)
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_0)) bgm.novas.tryShoot(2, 2)

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            ++bgmActiveLayers
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            --bgmActiveLayers
        }
        Assets.Music.values().let { layers ->
            bgmActiveLayers = bgmActiveLayers.coerceIn(0, layers.size)
            layers.forEachIndexed { index, music ->
                if (index < bgmActiveLayers) {
                    bgm.fadeIn(music)
                } else {
                    bgm.fadeOut(music)
                }
            }
        }

    }

    override fun onExit() {
        bgm.fadeOutAll()
    }
}
