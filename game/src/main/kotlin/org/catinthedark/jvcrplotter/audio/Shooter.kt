package org.catinthedark.jvcrplotter.audio

import com.badlogic.gdx.assets.AssetManager
import org.catinthedark.jvcrplotter.game.Assets
import org.catinthedark.jvcrplotter.game.at
import org.slf4j.LoggerFactory
import kotlin.math.log2
import kotlin.math.roundToInt


private const val MAX_PLAYERS = 4

private const val BPM = 94

class Shooter(
    private val am: AssetManager,
    private val slotsCount: Int = 8,
    private val beatsPerSlotter: Int = 2,
    private val eps: Float = 0.05f,
    private val slotToShoot: Array<Assets.Sounds> = arrayOf(
        Assets.Sounds.SHOOT_01,
        Assets.Sounds.SHOOT_02,
        Assets.Sounds.SHOOT_03,
        Assets.Sounds.SHOOT_04,
        Assets.Sounds.SHOOT_05,
        Assets.Sounds.SHOOT_02,
        Assets.Sounds.SHOOT_03,
        Assets.Sounds.SHOOT_04,
        Assets.Sounds.SHOOT_06,
        Assets.Sounds.SHOOT_02,
        Assets.Sounds.SHOOT_03,
        Assets.Sounds.SHOOT_04,
        Assets.Sounds.SHOOT_05,
        Assets.Sounds.SHOOT_02,
        Assets.Sounds.SHOOT_03,
        Assets.Sounds.SHOOT_04,
    )
) {
    private val maxSpeed = 1 + log2(slotsCount.toDouble()).roundToInt()

    private val logger = LoggerFactory.getLogger(javaClass)

    private val bulletSlots = HashMap<Int, Int>()
    private var lastSoundedSlot: Int? = null

    /**
     * Прогресс текущего бита от 0 до 1
     */
    private val slotterProgress: Float
        get() = ((am.at(Assets.Music.HI_TRASH).position * BPM / 60) / beatsPerSlotter % 1.0f)

    fun tryShoot(player: Int, speed: Int): Boolean {
        check(player in 0 until MAX_PLAYERS)
        check(speed in 1..maxSpeed)

        val currentSlot = getCurrentShootingSlot()

        if (lastSoundedSlot != currentSlot) {
            lastSoundedSlot = null
        }

        val shiftedCurrentSlot = currentSlot?.let {
            (it + player) % slotsCount
        } ?: return false

        check(shiftedCurrentSlot in 0 until slotsCount)

        if (bulletSlots[player] == shiftedCurrentSlot) {
            return false
        }

        if (shiftedCurrentSlot % (maxSpeed - speed + 1) != 0) {
            return false
        }

        if (lastSoundedSlot != currentSlot) {
            am.at(slotToShoot[currentSlot]).play()
            lastSoundedSlot = currentSlot
        }
        bulletSlots[player] = shiftedCurrentSlot
        return true
    }

    fun tryShootIf(player: Int, speed: Int, func: () -> Unit) {
        if (tryShoot(player, speed)) {
            func()
        }
    }

    private fun getCurrentShootingSlot(): Int? {
        val floatSlot = (slotterProgress + eps / 10).let { if (it >= 1) it - 1 else it }.let { it * slotsCount }

        val intSlot = floatSlot.toInt()

        val dist = (floatSlot - intSlot) / slotsCount

        return intSlot.takeIf { dist < eps }
    }
}
