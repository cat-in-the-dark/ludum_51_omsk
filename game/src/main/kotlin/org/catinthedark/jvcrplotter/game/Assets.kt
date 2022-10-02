package org.catinthedark.jvcrplotter.game

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
        val LOGO = "textures/title.png"
        val TITLE = "textures/title.png"

        val textures = listOf(
            LOGO,
            TITLE,
        )
    }

    enum class Sounds(val path: String) {
        POWERUP("sounds/powerup.wav"),
        SHOOT_01("sounds/1_sh.wav"),
        SHOOT_02("sounds/2_sh.wav"),
        SHOOT_03("sounds/3_sh.wav"),
        SHOOT_04("sounds/4_sh.wav"),
        SHOOT_05("sounds/5_sh.wav"),
        SHOOT_06("sounds/6_sh.wav"),
    }

    enum class Music(val path: String) {
//        KICK02("music/3_kick02.ogg"), // TODO убрать лишние файлы ассетов
//        SNARE06("music/4_snare06.ogg"),
//        SNARE02("music/5_snare02.ogg"),
        LO_TRASH("music/2_lo-trash.ogg"),
        HI_TRASH("music/1_hi-trash.ogg"),
    }
}

inline fun <reified T> AssetManager.at(name: String): T {
    return get(name, T::class.java)
}

fun AssetManager.load(sound: Assets.Sounds) = load(sound.path, Sound::class.java)
fun AssetManager.at(sound: Assets.Sounds): Sound = get(sound.path, Sound::class.java)

fun AssetManager.load(sound: Assets.Music) = load(sound.path, Music::class.java)
fun AssetManager.at(sound: Assets.Music): Music = get(sound.path, Music::class.java)
