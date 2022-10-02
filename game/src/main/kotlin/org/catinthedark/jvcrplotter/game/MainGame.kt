package org.catinthedark.jvcrplotter.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import org.catinthedark.jvcrplotter.game.states.*
import org.catinthedark.jvcrplotter.lib.Deffer
import org.catinthedark.jvcrplotter.lib.DefferImpl
import org.catinthedark.jvcrplotter.lib.IOC
import org.catinthedark.jvcrplotter.lib.states.StateMachine

class MainGame : Game() {
    private val stage: Stage by lazy {
        Stage(
            FitViewport(
                Const.Screen.WIDTH / Const.Screen.ZOOM,
                Const.Screen.HEIGHT / Const.Screen.ZOOM,
                OrthographicCamera()
            ), SpriteBatch()
        )
    }
    private val hud: Stage by lazy {
        Stage(
            FitViewport(
                Const.Screen.WIDTH / Const.Screen.ZOOM,
                Const.Screen.HEIGHT / Const.Screen.ZOOM,
                OrthographicCamera()
            ), SpriteBatch()
        )
    }
    private val sm: StateMachine by lazy {
        StateMachine().apply {
            putAll(
                States.SPLASH_SCREEN to SplashScreenState(),
                States.PLAYER_SCREEN to PlayerState(),
                States.TITLE_SCREEN to TitleScreenState(),
                States.GAME_OVER_SCREEN to GameOverState(),
                States.TEST_AUDIO_SCREEN to TestAudioState(),
            )
            putMixin(States.TITLE_SCREEN) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                    IOC.put("state", States.SPLASH_SCREEN)
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
                    IOC.put("state", States.TEST_AUDIO_SCREEN)
                }
                if (Gdx.input.isKeyPressed(Input.Keys.P)) {
                    IOC.put("state", States.PLAYER_SCREEN)
                }
            }
        }
    }
    private val shapeRenderer: ShapeRenderer by lazy {
        ShapeRenderer().apply {
            transformMatrix.scale(1.0f, -1.0f, 1.0f)
            transformMatrix.translate(0.0f, -Const.Screen.HEIGHT.toFloat(), 0.0f)
        }
    }

    override fun create() {
        IOC.put("deffer", DefferImpl())
        IOC.put("stage", stage)
        IOC.put("hud", hud)
        IOC.put("shapeRenderer", shapeRenderer)
        IOC.put("state", States.SPLASH_SCREEN)
    }

    override fun render() {
        val deffer: Deffer by IOC

        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        Gdx.gl.glLineWidth(stage.viewport.screenWidth / stage.viewport.worldWidth * 4f)

        shapeRenderer.projectionMatrix = stage.batch.projectionMatrix

        stage.viewport.apply()
        stage.act(Gdx.graphics.deltaTime)
        stage.batch.projectionMatrix = stage.viewport.camera.combined

        hud.viewport.apply()
        hud.act(Gdx.graphics.deltaTime)
        hud.batch.projectionMatrix = hud.viewport.camera.combined

        deffer.update(Gdx.graphics.deltaTime)
        sm.onUpdate()
        stage.draw()
        hud.draw()

        super.render()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        stage.viewport.update(width, height)
        hud.viewport.update(width, height)
    }
}
