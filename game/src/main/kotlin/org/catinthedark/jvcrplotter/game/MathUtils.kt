package org.catinthedark.jvcrplotter.game

import com.badlogic.gdx.math.Vector2
import kotlin.random.Random

fun randomDir(): Vector2 {
    val x = Random.nextFloat()
    val y = Random.nextFloat()
    return Vector2(x * 2f - 1f, y * 2f - 1f)
}
