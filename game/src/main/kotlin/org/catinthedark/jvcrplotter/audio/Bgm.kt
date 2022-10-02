package org.catinthedark.jvcrplotter.audio

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import org.catinthedark.jvcrplotter.game.Assets
import org.catinthedark.jvcrplotter.game.at
import org.slf4j.LoggerFactory
import kotlin.math.abs
import kotlin.math.log2
import kotlin.math.roundToInt

private const val DEFAULT_FADE_FORCE = 1.0f

class Bgm(
    private val am: AssetManager
) {
    val bullets = Shooter(am)

    private val logger = LoggerFactory.getLogger(javaClass)

    private val volumeGains = HashMap<Assets.Music, Float>()

    init {
        Assets.Music.values().forEach {
            volumeGains[it] = 0.0f
            am.at(it).apply {
                volume = 0.0f
                isLooping = true
            }
        }
        Assets.Music.values().forEach {
            am.at(it).apply {
                play()
                position = am.at(Assets.Music.values()[0]).position
            }
        }
    }

    fun update(dt: Float = Gdx.graphics.deltaTime) = Assets.Music.values().forEach {
        am.at(it).apply {
            volume = (volume + dt * (volumeGains[it] ?: 0.0f)).coerceIn(0.0f, 1.0f)
        }
    }

    fun fadeIn(music: Assets.Music, force: Float = DEFAULT_FADE_FORCE) {
        volumeGains[music] = abs(force)
    }

    fun fadeOut(music: Assets.Music, force: Float = DEFAULT_FADE_FORCE) {
        volumeGains[music] = -abs(force)
    }

    fun fadeInAll(force: Float = DEFAULT_FADE_FORCE) = Assets.Music.values().forEach { fadeIn(it, force) }

    fun fadeOutAll(force: Float = DEFAULT_FADE_FORCE) = Assets.Music.values().forEach { fadeOut(it, force) }
}
