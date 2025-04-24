package org.catinthedark.alyoep.audio

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import org.catinthedark.alyoep.game.Assets
import org.catinthedark.alyoep.game.at
import kotlin.math.abs

private const val DEFAULT_FADE_FORCE = 0.4f

class Bgm(
    private val am: AssetManager
) {
    val bullets = Shooter(
        am,
        slotsCount = 8,
        beatsPerSlotter = 2,
        slotToShoot = listOf(
            Assets.Sounds.SHOOT_01,
            Assets.Sounds.SHOOT_02,
            Assets.Sounds.SHOOT_03,
            Assets.Sounds.SHOOT_04,
            Assets.Sounds.SHOOT_05,
            Assets.Sounds.SHOOT_02,
            Assets.Sounds.SHOOT_03,
            Assets.Sounds.SHOOT_04,
        )
    )

    val playerHits = Shooter(
        am,
        slotsCount = 4,
        beatsPerSlotter = 2,
        slotToShoot = listOf(
            Assets.Sounds.HIT_01,
            Assets.Sounds.HIT_02,
            Assets.Sounds.HIT_03,
            Assets.Sounds.HIT_04,
        )
    )

    val towerHits = Shooter(
        am,
        slotsCount = 4,
        beatsPerSlotter = 2,
        slotToShoot = listOf(
            Assets.Sounds.TOWER_HIT_01,
            Assets.Sounds.TOWER_HIT_02,
            Assets.Sounds.TOWER_HIT_03,
            Assets.Sounds.TOWER_HIT_04,
        )
    )

    val novas = Shooter(
        am,
        slotsCount = 16,
        beatsPerSlotter = 16,
        slotToShoot = sequenceOf(
            IntRange(1, 4).map { Assets.Sounds.NOVA_05 },
            IntRange(1, 4).map { Assets.Sounds.NOVA_01 },
            IntRange(1, 4).map { Assets.Sounds.NOVA_02 },
            IntRange(1, 2).map { Assets.Sounds.NOVA_03 },
            IntRange(1, 2).map { Assets.Sounds.NOVA_04 },
        ).flatMap { it }.toList()
    )

    private val volumeGains = HashMap<Assets.Music, Float>()

    init {
        Assets.Music.values().forEach {
            volumeGains[it] = 0.0f
            am.at(it).apply {
                volume = 0.0f
                isLooping = true
            }
        }
        resyncMusic()
        am.at(Assets.Music.BASS).setOnCompletionListener {
            resyncMusic()
        }
    }

    private fun resyncMusic() {
        // TODO resync on window resize!

        Assets.Music.values().asSequence().forEach {
            am.at(it).apply {
                play()
                position = am.at(Assets.Music.BASS).position
            }
        }
    }


    fun update(dt: Float = Gdx.graphics.deltaTime) = Assets.Music.values().forEach {
        am.at(it).apply {
            volume = (volume + dt * (volumeGains[it] ?: 0.0f)).coerceIn(0.0f, 1.0f)
        }
    }

    fun updateLayers(maxBossness: Int, playersCount: Int) {
        fadeOutAll()
        fadeOut(Assets.Music.MEGABOSS, DEFAULT_FADE_FORCE / 3)
        if (playersCount <= 0) {
            fadeIn(Assets.Music.LO_TRASH)
        } else {
            fadeIn(Assets.Music.BASS)
            if (maxBossness > 1) {
                fadeIn(Assets.Music.MEGABOSS, DEFAULT_FADE_FORCE / 8)
                fadeOut(Assets.Music.BASS)
            } else if (maxBossness > 0) {
                fadeIn(Assets.Music.HI_TRASH)
            }
        }
    }

    fun collectPowerup(sound: Assets.Sounds = Assets.Sounds.POWERUP) {
        am.at(sound).play()
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
