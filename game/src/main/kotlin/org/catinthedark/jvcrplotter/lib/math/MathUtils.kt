package org.catinthedark.jvcrplotter.lib.math

import com.badlogic.gdx.math.Vector2
import org.catinthedark.jvcrplotter.lib.ITransform
import kotlin.random.Random

fun randomDir(): Vector2 {
    val x = Random.nextFloat()
    val y = Random.nextFloat()
    return Vector2(x * 2f - 1f, y * 2f - 1f).nor()
}

fun findClosest(from: ITransform, others: List<ITransform>): ITransform? {
    var minDist = Float.MAX_VALUE
    var target: ITransform? = null
    others.forEach {
        val dst = it.pos.dst2(from.pos)
        if (dst < minDist) {
            minDist = dst
            target = it
        }
    }
    return target
}