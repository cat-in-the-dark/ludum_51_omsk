package org.catinthedark.jvcrplotter.lib

import com.badlogic.gdx.Gdx
import org.catinthedark.jvcrplotter.lib.interfaces.IUpdatable

class CoolDown(
    val time: Float
) : IUpdatable {
    private var timer: Float = 0f

    override fun update() {
        timer += Gdx.graphics.deltaTime
    }

    fun invoke(func: (Float) -> Unit) {
        if (timer >= time) {
            func(timer)
            timer = 0f
        }
        // else wait
    }
}
