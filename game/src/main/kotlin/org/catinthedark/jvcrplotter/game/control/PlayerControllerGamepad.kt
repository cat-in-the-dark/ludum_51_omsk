package org.catinthedark.jvcrplotter.game.control

import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.math.Vector2
import org.slf4j.LoggerFactory
import kotlin.math.abs

class PlayerControllerGamepad(private val controller: Controller) : PlayerController {
    private val threshHold = 0.05f
    override fun getDirection(): Vector2 {
        var x = controller.getAxis(controller.mapping.axisRightX)
        var x1 = controller.getAxis(controller.mapping.axisLeftX)
        if (abs(x) < abs(x1)) {
            x = x1
        }
        if (controller.getButton(controller.mapping.buttonDpadLeft)) {
            x1 = -1.0f
        } else if (controller.getButton(controller.mapping.buttonDpadRight)) {
            x1 = 1.0f
        }
        if (abs(x) < abs(x1)) {
            x = x1
        }

        var y = controller.getAxis(controller.mapping.axisRightY)
        var y1 = controller.getAxis(controller.mapping.axisLeftY)
        if (abs(y) < abs(y1)) {
            y = y1
        }
        if (controller.getButton(controller.mapping.buttonDpadUp)) {
            y1 = -1.0f
        } else if (controller.getButton(controller.mapping.buttonDpadDown)) {
            y1 = 1.0f
        }
        if (abs(y) < abs(y1)) {
            y = y1
        }

        if (abs(x) < threshHold) {
            x = 0f
        }
        if (abs(y) < threshHold) {
            y = 0f
        }

        return Vector2(x, y)
    }
}
