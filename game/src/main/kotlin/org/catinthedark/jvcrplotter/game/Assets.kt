package org.catinthedark.jvcrplotter.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture

object Assets {
    fun load(): AssetManager {
        return AssetManager().apply {
            Names.textures.forEach { load(it, Texture::class.java) }
            Sounds.values().forEach(::load)
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
        POWERUP("sounds/powerup.wav")
    }
}

inline fun <reified T> AssetManager.at(name: String): T {
    return get(name, T::class.java)
}

fun AssetManager.load(sound: Assets.Sounds): Unit = load(sound.path, Sound::class.java)
fun AssetManager.at(sound: Assets.Sounds): Sound = get(sound.path, Sound::class.java)
fun AssetManager.isLoaded(sound: Assets.Sounds): Boolean = isLoaded(sound.path, Sound::class.java)
