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

private const val MAX_PLAYERS = 4

private const val BPM = 94

private const val SHOOTING_SLOTS = 8

private val MAX_SPEED = 1 + log2(SHOOTING_SLOTS.toDouble()).roundToInt()

private const val EPSILON = 0.05f

private const val BEATS_PER_SLOTTER = 2

class Bgm(
    private val am: AssetManager
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val volumeGains = HashMap<Assets.Music, Float>()

    private val bulletSlots = HashMap<Int, Int>()
    private var lastSoundedSlot: Int? = null

    init {
        Assets.Music.values().forEach {
            volumeGains[it] = 0.0f
            am.at(it).apply {
                volume = 0.0f
                isLooping = true
                play()
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

    /**
     * Прогресс текущего бита от 0 до 1
     */
    private val slotterProgress: Float
        get() = ((am.at(Assets.Music.HI_TRASH).position * BPM / 60) / BEATS_PER_SLOTTER % 1.0f)

    fun tryShoot(player: Int, speed: Int): Boolean {
        check(player in 0 until MAX_PLAYERS)
        check(speed in 1..MAX_SPEED)

        val currentSlot = getCurrentShootingSlot()

        if (lastSoundedSlot != currentSlot) {
            lastSoundedSlot = null
        }

        val shiftedCurrentSlot = currentSlot?.let {
            (it + player) % SHOOTING_SLOTS
        } ?: return false

        check(shiftedCurrentSlot in 0 until SHOOTING_SLOTS)

        if (bulletSlots[player] == shiftedCurrentSlot) {
            return false
        }

        if (shiftedCurrentSlot % (MAX_SPEED - speed + 1) != 0) {
            return false
        }

        if (lastSoundedSlot != currentSlot) {
            am.at(Assets.Sounds.SHOOT).play()
            lastSoundedSlot = currentSlot
        }
        bulletSlots[player] = shiftedCurrentSlot
        return true
    }

    private fun getCurrentShootingSlot(): Int? {
        val floatSlot = (slotterProgress + EPSILON)
            .let { if (it >= 1) it - 1 else it }
            .let { it * SHOOTING_SLOTS }

        val intSlot = floatSlot.toInt()

        val dist = (floatSlot - intSlot) / SHOOTING_SLOTS

        return intSlot.takeIf { dist < EPSILON }
    }
}
