package org.catinthedark.alyoep.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import org.catinthedark.alyoep.audio.Bgm
import org.catinthedark.alyoep.game.Assets
import org.catinthedark.alyoep.game.Const
import org.catinthedark.alyoep.game.States
import org.catinthedark.alyoep.game.control.PlayerController
import org.catinthedark.alyoep.game.texture
import org.catinthedark.alyoep.lib.AfterBarrier
import org.catinthedark.alyoep.lib.IOC
import org.catinthedark.alyoep.lib.atOrFail
import org.catinthedark.alyoep.lib.managed
import org.catinthedark.alyoep.lib.states.IState

class TitleScreenState : IState {
    private val hud: Stage by lazy { IOC.atOrFail<Stage>("hud") }
    private val am: AssetManager by lazy { IOC.atOrFail<AssetManager>("assetManager") }
    private val bgm: Bgm by lazy { IOC.atOrFail("bgm") }
    private val controllers: Map<PlayerController, Boolean> = IOC.atOrFail("input")
    private val render: ShapeRenderer by lazy { IOC.atOrFail("shapeRenderer") }

    private val drawNextBarrier = AfterBarrier(2f)

    private var currentTime = 0f

    override fun onActivate() {
        currentTime = 0f
        drawNextBarrier.reset()
    }

    override fun onUpdate() {
        bgm.update()
        hud.batch.managed { b ->
            b.draw(am.texture(Assets.Names.TITLE), 0f, 0f)
        }

        currentTime += Gdx.graphics.deltaTime
        drawNextBarrier.invoke {
            render.managed(ShapeRenderer.ShapeType.Line) {
                it.color = Color.WHITE
                val spacePos = Vector2(
                    Const.Screen.WIDTH / 2f - (TutorialState.Dimensions.SPACE_WIDTH + TutorialState.Dimensions.ARROW_WIDTH + 2 * TutorialState.Dimensions.BUTTON_SPACING) / 2,
                    Const.Screen.HEIGHT - 100f
                )

                TutorialState.DrawHelpers.drawSpaceBar(it, spacePos)

                val arrowPos = spacePos.cpy()
                    .add(TutorialState.Dimensions.SPACE_WIDTH + 2 * TutorialState.Dimensions.BUTTON_SPACING, 15f)
                TutorialState.DrawHelpers.animateArrow(
                    currentTime,
                    it,
                    arrowPos,
                    TutorialState.Dimensions.ARROW_WIDTH,
                    45f
                )
            }
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
