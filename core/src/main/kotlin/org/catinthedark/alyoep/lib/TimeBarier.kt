package org.catinthedark.alyoep.lib

import com.badlogic.gdx.Gdx
import kotlin.math.min

private fun delta() = Gdx.graphics.deltaTime

interface ITimeBarrier {
    operator fun invoke(func: () -> Unit)
    fun reset()
}


class OnceBarrier(
    private val after: Float
) : ITimeBarrier {
    private var time = 0f
    private var called = false

    override fun invoke(func: () -> Unit) {
        time += delta()

        if (time >= after && !called) {
            called = true
            func()
        }
    }

    override fun reset() {
        time = 0f
        called = false
    }

    val timeAmount
        get() = (after - min(time, after)) / after
}

class AfterBarrier(
    private val after: Float
) : ITimeBarrier {
    private var time = 0f

    override fun invoke(func: () -> Unit) {
        time += delta()
        if (time >= after) func()
    }

    override fun reset() {
        time = 0f
    }
}

class RepeatBarrier(
    private val after: Float,
    private val interval: Float = after
) : ITimeBarrier {
    private var time = 0f
    private var called = false
    var count = 0

    override fun invoke(func: () -> Unit) {
        time += delta()
        if (called) {
            if (time >= interval) {
                time = 0f
                func()
                count++
            }
        } else {
            if (time >= after) {
                time = 0f
                func()
                count++
                called = true
            }
        }

    }

    override fun reset() {
        time = 0f
        count = 0
        called = false
    }
}
