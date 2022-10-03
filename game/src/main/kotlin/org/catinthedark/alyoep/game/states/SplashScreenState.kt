package org.catinthedark.alyoep.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Stage
import org.catinthedark.alyoep.audio.Bgm
import org.catinthedark.alyoep.game.Assets
import org.catinthedark.alyoep.game.States
import org.catinthedark.alyoep.lib.IOC
import org.catinthedark.alyoep.lib.atOrFail
import org.catinthedark.alyoep.lib.managed
import org.catinthedark.alyoep.lib.states.IState
import org.slf4j.LoggerFactory

class SplashScreenState : IState {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val hud: Stage by lazy { IOC.atOrFail<Stage>("hud") }
    private val am: AssetManager by lazy { Assets.load() }
    private val bgm: Bgm by lazy { Bgm(am) }
    private var time = 0f

    override fun onActivate() {
        time = 0f
        IOC.put("showTutorial", true)
    }

    override fun onUpdate() {
        time += Gdx.graphics.deltaTime
        if (am.update()) {
            IOC.put("state", States.TITLE_SCREEN)
            IOC.put("assetManager", am)
            IOC.put("bgm", bgm)
            logger.info("Assets loaded in $time")
        } else {
            logger.info("Loading assets...${am.progress}")
        }

        if (am.isLoaded(Assets.Names.LOGO, Texture::class.java)) {
            hud.batch.managed {
                it.draw(am.get(Assets.Names.LOGO, Texture::class.java), 0f, 0f)
            }
        }
    }

    override fun onExit() {
    }
}
