package org.catinthedark.alyoep.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture

object Assets {
    fun load(): AssetManager {
        return AssetManager().apply {
            Names.textures.forEach { load(it, Texture::class.java) }
            Sounds.values().forEach(::load)
            Music.values().forEach(::load)
        }
    }

    object Names {
        val LOGO = "textures/logo.png"
        val TITLE = "textures/title.png"
        val GAME_OVR = "textures/game_over.png"

        val textures = listOf(
            LOGO,
            TITLE,
            GAME_OVR,
        )
    }

    enum class Sounds(val path: String) {
        POWERUP("sounds/powerup.wav"),
        HEAL("sounds/heal.wav"),

        SHOOT_01("sounds/1_sh.wav"),
        SHOOT_02("sounds/2_sh.wav"),
        SHOOT_03("sounds/3_sh.wav"),
        SHOOT_04("sounds/4_sh.wav"),
        SHOOT_05("sounds/5_sh.wav"),
        SHOOT_06("sounds/6_sh.wav"),

        NOVA_01("sounds/nova/1_SID.wav"),
        NOVA_02("sounds/nova/2_SID.wav"),
        NOVA_03("sounds/nova/3_SID.wav"),
        NOVA_04("sounds/nova/4_SID.wav"),
        NOVA_05("sounds/nova/5_SID.wav"),

        HIT_01("sounds/hits/hit_1.wav"),
        HIT_02("sounds/hits/hit_2.wav"),
        HIT_03("sounds/hits/hit_3.wav"),
        HIT_04("sounds/hits/hit_4.wav"),

        TOWER_HIT_01("sounds/hits/Explosion (1).wav"),
        TOWER_HIT_02("sounds/hits/Explosion (2).wav"),
        TOWER_HIT_03("sounds/hits/Explosion (3).wav"),
        TOWER_HIT_04("sounds/hits/Explosion (4).wav"),
    }

    enum class Music(val path: String) {
        BASS("music/4_bass.ogg"),
        DREAM("music/3_dream.ogg"),
        HI_TRASH("music/2_hi-trash.ogg"),
        LO_TRASH("music/1_lo-trash.ogg"),
    }
}

inline fun <reified T> AssetManager.at(name: String): T {
    return get(name, T::class.java)
}

fun AssetManager.load(sound: Assets.Sounds) = load(sound.path, Sound::class.java)
fun AssetManager.at(sound: Assets.Sounds): Sound = get(sound.path, Sound::class.java)

fun AssetManager.load(sound: Assets.Music) = load(sound.path, Music::class.java)
fun AssetManager.at(sound: Assets.Music): Music = get(sound.path, Music::class.java)
